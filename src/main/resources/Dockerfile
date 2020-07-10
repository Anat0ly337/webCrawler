FROM openjdk:13-alpine AS builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN jar -xf ./app.jar
RUN { \
       java --version ; \
       echo "jlink version:" && \
       jlink --version ; \
   }
ENV JAVA_MINIMAL=/opt/jre
RUN jlink \
   --verbose \
   --add-modules \
       java.base,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.unsupported,java.logging,java.xml \
   --compress 2 \
   --no-header-files \
   --no-man-pages \
   --output "$JAVA_MINIMAL"

FROM alpine
ENV JAVA_MINIMAL=/opt/jre
ENV PATH="$PATH:$JAVA_MINIMAL/bin"
COPY --from=builder "$JAVA_MINIMAL" "$JAVA_MINIMAL"
COPY --from=builder BOOT-INF/lib /app/lib
COPY --from=builder META-INF /app/META-INF
COPY --from=builder BOOT-INF/classes /app
EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","com.godeltech.pt11.PtApplication"]
