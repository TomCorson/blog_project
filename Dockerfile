FROM openjdk:8
ADD target/blog_project.jar blog_project.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "blog_project.jar"]
