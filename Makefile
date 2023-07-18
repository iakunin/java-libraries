.PHONY: .gradle .idea build etc gradle src

# :src:logging:logging-servlet
logging-servlet-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:logging:logging-servlet:clean :src:logging:logging-servlet:publishToMavenLocal

logging-servlet-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:logging:logging-servlet:clean :src:logging:logging-servlet:publish


# :src:logging:logging-reactive
logging-reactive-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:logging:logging-reactive:clean :src:logging:logging-reactive:publishToMavenLocal

logging-reactive-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:logging:logging-reactive:clean :src:logging:logging-reactive:publish


# :src:logging:logging-common
logging-common-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:logging:logging-common:clean :src:logging:logging-common:publishToMavenLocal

logging-common-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:logging:logging-common:clean :src:logging:logging-common:publish


# :src:feign-tracing
feign-tracing-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:feign-tracing:clean :src:feign-tracing:publishToMavenLocal

feign-tracing-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:feign-tracing:clean :src:feign-tracing:publish


# :src:exception-handling
exception-handling-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:exception-handling:clean :src:exception-handling:publishToMavenLocal

exception-handling-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:exception-handling:clean :src:exception-handling:publish


# :src:tests
tests-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:tests:clean :src:tests:publishToMavenLocal

tests-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:tests:clean :src:tests:publish


# :src:persistence
persistence-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:persistence:clean :src:persistence:publishToMavenLocal

persistence-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:persistence:clean :src:persistence:publish


# :src:kafka:key-value-storage
persistence-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:kafka:kafka-key-value-storage:clean :src:kafka:kafka-key-value-storage:publishToMavenLocal

persistence-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:kafka:kafka-key-value-storage:clean :src:kafka:kafka-key-value-storage:publish
