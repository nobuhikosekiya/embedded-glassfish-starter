package com.example.embeddedglassfishstarter;


import java.io.File;
import java.util.Optional;

import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

public class EmbeddedGlassFish {

	public static void main(String[] args) throws GlassFishException {
		GlassFishProperties glassfishProperties = new GlassFishProperties();
	    String port = Optional.ofNullable(System.getenv("PORT")).orElse("8080");
	    
	    String warName = Optional.ofNullable(System.getProperty("appfile.name")).orElse("app.war");
	    String contextroot = Optional.ofNullable(System.getProperty("contextroot")).orElse("root");
	    
		glassfishProperties.setPort("http-listener", Integer.parseInt(port));
//		glassfishProperties.setPort("https-listener", 8181);

		final GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					glassfish.stop();
					glassfish.dispose();
					System.err.println("GlassFish load error");
				} catch (GlassFishException e) {
					e.printStackTrace();
				}
			}
		}));

		glassfish.start();
		System.err.println("GlassFish starting");

		File war = new File(warName);
		if (!war.exists()) {
			System.err.println(warName + " not found");
			throw new GlassFishException("startup error: " + warName + " not found.");
		}
		Deployer deployer = glassfish.getDeployer();
		deployer.deploy(war, "--name=app", "--contextroot=" + contextroot, "--force=true");
		System.err.println(war + " started with --name=app --contextroot=" + contextroot + " --force=true");
	}

}
