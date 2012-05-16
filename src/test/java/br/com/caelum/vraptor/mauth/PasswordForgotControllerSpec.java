package br.com.caelum.vraptor.mauth;

import static br.com.caelum.vraptor.Option.none;
import static br.com.caelum.vraptor.Option.some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.mauth.user.NavigationInfo;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.view.HttpResult;

@RunWith(MockitoJUnitRunner.class)
public class PasswordForgotControllerSpec {

	@Mock
	private AuthUserRepository users;
	@Mock
	private HttpResult http;
	@Mock
	private Transaction transaction;

	private MockResult result;
	private MockValidator validator;
	private PasswordForgotController controller;

	@Before
	public void before() {
		this.result = spy(new MockResult());
		this.validator = spy(new MockValidator());
		this.controller = new PasswordForgotController(result, users,
				transaction, validator);
	}

	@Test
	public void complain_if_user_email_is_not_found() {
		when(users.findByEmail(null)).thenReturn(noUser());

		SystemUser guilherme = new User();
		when(users.findByEmail(guilherme.getEmail())).thenReturn(noUser());

		when(result.use(HttpResult.class)).thenReturn(http);
		controller.forgotPassword(guilherme.getEmail());
		verify(result).include("error", "vraptor.email_not_found");
	}

	@Test
	public void send_email_to_user_when_valid_email_is_passed() {
		SystemUser user = new User();

		when(users.findByEmail(user.getEmail())).thenReturn(some(user));

		controller.forgotPassword(user.getEmail());
		verify(mails).dispatch(any(PasswordForgotMail.class));
	}

	@Test(expected = ValidationException.class)
	public void redirect_to_login_page_if_invalid_newPasswordToken_is_passed() {
		when(users.findForEncryptedURL(null)).thenReturn(noUser());
		controller.resetPassword(null);
	}

	private Option<SystemUser> noUser() {
		return none();
	}

	@Test
	public void change_password_if_a_valid_newPasswordToken_is_passed() {
		SystemUser user = new User();
		String newPasswordToken = user.getPassword()
				.generateEncryptedRecoveryText(user.getEmail());
		when(users.findForEncryptedURL(newPasswordToken))
				.thenReturn(some(user));

		controller.reassignPassword("someNewPassword", newPasswordToken);

		assertEquals(Digester.encrypt("someNewPassword"), user.getPassword()
				.getPassword());
		assertNotSame(newPasswordToken, user.getPassword()
				.getLastEncryptedRecoveryURL());
	}
}

class User implements SystemUser {

	private Password password = new Password();

	@Override
	public String getEmail() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setToken(String token) {
	}

	@Override
	public String getToken() {
		return null;
	}

	@Override
	public Serializable getId() {
		return null;
	}

	@Override
	public Password getPassword() {
		return this.password;
	}

	@Override
	public NavigationInfo getNavigationInfo() {
		return null;
	}

}
