## Zak's Spring Boot Starter Discord

This project is here to be nice. It isn't very clean, but it works, and it works well. There are still remnants of my old utils in here

Use the annotation `@EnableDiscord` to enable listener registering, command registering, etc...

Regarding `JDA` instances, you can make your own and register it as a bean, otherwise we will create an instance for you. Check the `JdaConfiguration` class for how this is done.

Documentation: The documentation is the code, sorry.
Support: Make an issue/discussion