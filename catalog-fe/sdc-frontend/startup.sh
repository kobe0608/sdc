#!/bin/sh

JAVA_OPTIONS=" ${JAVA_OPTIONS} -Dconfig.home=${JETTY_BASE}/config -Dlog.home=${JETTY_BASE}/logs -Dlogback.configurationFile=${JETTY_BASE}/config/catalog-fe/logback.xml -Dconfiguration.yaml=${JETTY_BASE}/config/catalog-fe/configuration.yaml -Donboarding_configuration.yaml=${JETTY_BASE}/config/onboarding-fe/onboarding_configuration.yaml"

cd /root/chef-solo
chef-solo -c solo.rb -E ${ENVNAME}

cd /var/lib/jetty
/docker-entrypoint.sh &

while true; do sleep 2; done

