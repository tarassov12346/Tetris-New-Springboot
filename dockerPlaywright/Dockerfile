FROM mcr.microsoft.com/playwright/java:v1.30.0-focal

RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y xvfb
RUN apt-get install -qqy x11-apps

# chromium dependencies
RUN apt-get install -y libnss3 \
                       libxss1 \
                       libasound2 \
                       fonts-noto-color-emoji

# additional actions related to your project

ENTRYPOINT ["/bin/sh", "-c", "/usr/bin/xvfb-run -a $@", ""]  # exactly this kind of magic command :)