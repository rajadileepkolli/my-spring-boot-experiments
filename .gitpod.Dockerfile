FROM gitpod/workspace-full

USER root

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh \
             && sdk install java 19.0.1-open \
             && sdk install java 22.2.r17-grl \
             && sdk install java 18.0.1-amzn \
             && sdk default java 19.0.1-open"