package br.com.caelum.vraptor.mauth.facebook;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.mauth.Open;
import br.com.caelum.vraptor.mauth.SystemUser;
import br.com.caelum.vraptor.mauth.Transaction;

@Resource
@Open
public class FacebookLoginController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory
			.getLogger(FacebookLoginController.class);

	private final Transaction transaction;

	private final Facebook facebook;

	public FacebookLoginController(Transaction transaction,
			Facebook facebook) {
		this.transaction = transaction;
		this.facebook = facebook;
	}

	@Post("/auth/facebook")
	public void login(final String profile) {
		LOGGER.debug("logging in user " + profile);
		final SystemUser user = facebook.connectedOrFindUserFor(profile);
		transaction.execute(new Runnable() {
			public void run() {
				facebook.createOrConnectUser(profile, user);
			}
		});

	}

}
