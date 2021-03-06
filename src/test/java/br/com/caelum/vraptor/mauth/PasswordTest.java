package br.com.caelum.vraptor.mauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordTest {

	@Test(expected = IllegalArgumentException.class)
	public void should_complain_if_the_password_is_null_when_changing_it() {
		new Password().changeTo(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldComplainIfItsEmptyWhenChangingIt() {
		new Password().changeTo("");
	}

	@Test
	public void shouldChangeThePasswordToAnEncryptedOne() {
		Password password = new Password();
		password.changeTo("old");
		assertEquals(Digester.encrypt("old"), password.getPassword());
	}

	@Test
	public void shouldNotMatchWithNull() {
		assertFalse(new Password().matchesWith(null));
	}

	@Test
	public void shouldMatchIfMatches() {
		Password password = new Password();
		password.changeTo("guilherme");
		assertTrue(password.matchesWith("guilherme"));
	}

	@Test
	public void shouldNotMatchIfNotMatches() {
		Password password = new Password();
		password.changeTo("guilherme");
		assertFalse(password.matchesWith("guilherme2"));
	}

	@Test
	public void shouldChangeLastEncryptedURLWhenCallingGenerate() {
		Password password = new Password();
		String emptyText = password.getLastEncryptedRecoveryURL();
		assertFalse(password.generateEncryptedRecoveryText(
				"guilherme.silveira@caelum.com.br").equals(emptyText));
	}

}
