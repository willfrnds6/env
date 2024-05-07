# Env library

This library allow you to add environment variable into a properties file

There is an example of how you can write your property file

```properties
your.property=${ENV_VARIABLE:defaultValue}
```
The default value is going to be used if the env variable is not found.

You can also not provide default value. The library is going to throw a NotFoundException, 
if the env variable can't be found.

There is an example
```properties
your.property=${ENV_VARIABLE}
```



## Installation

Compile the lib using maven commands
```shell
mvn clean install
```

Install the jar 
```shell
mvn install:install-file -Dfile=env-1.2.jar -DgroupId=fr.fernandes.will.env -DartifactId=env -Dversion=1.2 -Dpackaging=jar
```

Add this in your pom

```xml
<dependecy>
    <groupId>fr.fernandes.will</groupId>
    <artifactId>env</artifactId>
    <version>1.2</version>
</dependecy>
```

## Utilisation
There it is an example of utilisation

```java
import fr.fernandes.will.utils.env.Env;

import java.io.InputStream;
import java.util.Properties;

// Load property file
InputStream propertyFile = currentClass.getClassLoader().getResourceAsStream("path/to/application.properties");

// Send file to the lib
Env env = new Env(propertyFile);

// Get properties with env value
Properties props = env.updatePropertiesWithEnvVariable();
```
