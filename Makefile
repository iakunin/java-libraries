.PHONY: .gradle .idea build etc gradle src

# :src:logging
logging-publishToLocal:
	bash etc/bin/gradle_in_docker.sh :src:logging:clean :src:logging:publishToMavenLocal

logging-publishToRemote:
	bash etc/bin/gradle_in_docker.sh :src:logging:clean :src:logging:publish


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
