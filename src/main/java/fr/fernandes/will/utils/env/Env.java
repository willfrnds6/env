package fr.fernandes.will.utils.env;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.fernandes.will.utils.env.exception.NotFoundException;

public class Env {
    private static final Map<String, String> ENV = System.getenv();
    private static final String VARIABLE_REGEX = "(\\$\\{(?:[A-Z_]+)(?:\\:)?(?:[A-Za-z0-9-@#\\/_\\.:]+)?\\})";
    private final InputStream propertyFile;

    public Env(InputStream propertyFile) {
        this.propertyFile = propertyFile;
    }

    /**
     * Overwrite variable's of property file, by environment's variable
     *
     * @return updated properties
     * @throws IOException If an error occurred when reading from the input stream
     * @throws NotFoundException If the environment variable is not found and no default value is given
     */
    public Properties updatePropertiesWithEnvVariable() throws IOException, NotFoundException {
        // Load property file
        Properties properties = new Properties();
        properties.load(propertyFile);

        // Generate pattern
        Pattern pattern = Pattern.compile(VARIABLE_REGEX);

        // Initialize some variables here for better memory management
        String propertyFileValue;
        Matcher matcher;
        String currentDetectedGroup;
        String envValue;
        String propertyValueToCheck;
        String[] splitVal;
        String propertyToSet;
        boolean isLink = false;

        // Loop on all property
        for (Map.Entry<Object, Object> propertiesEntrySet : properties.entrySet()) {
            // Get value present in the property file
            propertyFileValue = propertiesEntrySet.getValue().toString();

            // Check if the value is supposed to be overwritten by env variable
            matcher = pattern.matcher(propertyFileValue);

            // Loop on all match found
            while (matcher.find()) {
                currentDetectedGroup = matcher.group();
                // Remove un wanted characters
                propertyValueToCheck = currentDetectedGroup
                        .replace("${", Constant.EMPTY_STRING)
                        .replace("}", Constant.EMPTY_STRING);

                // Check if there is a link
                if (propertyValueToCheck.contains("https://") || propertyValueToCheck.contains("http://")) {
                    isLink = true;
                    propertyValueToCheck = propertyValueToCheck
                            .replace("https://", Constant.EMPTY_STRING)
                            .replace("http://", Constant.EMPTY_STRING);
                }

                // Check if a default value is provided in the property file
                if (!propertyValueToCheck.contains(":")) {
                    // Get environment value
                    envValue = ENV.get(propertyValueToCheck);

                    // Env value doesn't exist, throw NotFoundException
                    if (envValue == null) {
                        throw new NotFoundException("Can't found " + propertyValueToCheck + " env variable");
                    }

                    // Set property to set on env value
                    propertyToSet = envValue;
                } else {
                    splitVal = propertyValueToCheck.split(":", -1);
                    envValue = ENV.get(splitVal[0]);

                    // Set default value if env variable not found
                    if (envValue == null) {
                        // If default variable not found, throw NotFoundException
                        if (splitVal[1] == null || splitVal[1].isBlank()) {
                            throw new NotFoundException(
                                    "Can't found default value for " + propertyValueToCheck + " env variable");
                        }

                        // Set property to set on default value found in property file
                        propertyToSet = splitVal[1];
                    }
                    // Set property to set on env value
                    else {
                        propertyToSet = envValue;
                    }
                }

                // Overwrite match
                propertyFileValue = propertyFileValue.replace(currentDetectedGroup, propertyToSet);

                // Add https
                if (isLink) {
                    propertyFileValue = "https://" + propertyFileValue;
                    isLink = false;
                }

                properties.setProperty(propertiesEntrySet.getKey().toString(), propertyFileValue);
            }
        }

        return properties;
    }
}
