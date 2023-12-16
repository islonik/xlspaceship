XL-Spaceship simulator.
==================================
XL-Spaceship is a guessing game (a modified version of battleship game) where you play against AI.

Technology stack: Java17, Spring framework, Spring Boot, Spring Mvc, HTML, JQuery, Ajax and Thymeleaf.

To be able to build this project you should have installed maven.
./build.sh - will build this project.

To be able to run this project: you should launch run.sh and runAI.sh files (you can customize these files in advance).

*******************************************
### How to enable pretty json output
*******************************************
<b>Attention!!!</b>

If you are running a boot app but elected to configure the web application yourself, either by @EnableWebMvc annotation 
or implementing WebMvcConfigurationSupport, then you need to configure your swagger-ui on your own (add resource handlers). 
The web jar will not be auto configured for you.

```java
@Configuration
public class OverrideDefaultConfiguration extends WebMvcConfigurationSupport {

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                jacksonConverter.setPrettyPrint(true);
            }
        }
    }

}
```
