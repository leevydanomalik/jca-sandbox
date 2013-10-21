<p>Build itself will patch the server, maven configuration for it is inside smoke pom.xml profile for eap.version=6.0.1
</p>
<p>
How it works? </br>
camel.rar dependes on contribution-set-module (using jboss-deployment-structure.xml). Test itself will replace module.xml for contribution-set-module which is empty by default with one that contains:
<code>
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="com.sample.edejket.camel.contrib">
	<resources>
	</resources>

	<dependencies>
		<module name="com.sample.edejket.camel.samplecontrib.customcomp"
			export="true" services="export">
		</module>
	</dependencies>
</module>
</code>
</br>File itself is located in testsuite/integration/smoke/src/test/resources/test_settings/custom_module_xml/module.xml file.

Rar - >dependes ->contrib-module -> depends on sample-contrib-module (contains jar with META-INF/service/org/apache/camel/component/customComp)</br>
That file will be looked up by camel when the rar is activated (start method of the rar, starts up camel contexts - > looks for components on classpath)
</p>
<p>
1. Run with mvn clean install -DintegrationTests -Deap.version=6.0.1
</p>
<p>
2.smoke project : target/arquillian contains arquillian built deployments, target/jboss-as-dist-jboss-eap-6.0.1 is EAP being used by test and configured by maven and arquillian.
</p>
<p>
3.smoke project: src/test/resources/jboss_settings/standalone/configuration/eap-6.0.1/standalone-full-ha.xml contains configuration file used to run server (logging can be adjusted there)
</p>
<p>
4.smoke project: src/test/resources/jboss_patch contains the patch for testing (will be copied by maven-resources-plugin)
</p>
