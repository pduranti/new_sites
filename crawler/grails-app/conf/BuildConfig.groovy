grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"

        mavenRepo "http://www.ibiblio.org/maven/mule/dependencies/maven2"
        mavenRepo "http://mirrors.ibiblio.org/pub/mirrors/maven2/"
        mavenRepo "http://git.ml.com:8081/nexus/content/groups/Arquitectura"
        mavenRepo "http://git.ml.com:8081/nexus/content/repositories/MLGrailsPlugins/org/grails/plugins/"
        mavenRepo "http://raykrueger.googlecode.com/svn/repository"
        mavenRepo "http://git.ml.com:8081/nexus/content/groups/public"
        mavenRepo "http://git.ml.com:8081/nexus/content/groups/public_"
        mavenRepo "http://maven.glassfish.org/content/groups/glassfish/"
        mavenRepo "http://repo.grails.org/grails/plugins/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        runtime 'edu.uci.ics:crawler4j:4.2'
        runtime 'org.jsoup:jsoup:1.9.2'
    }
    plugins {
        build ":tomcat:7.0.55"
    }
}
