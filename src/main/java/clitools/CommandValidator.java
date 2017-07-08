package clitools;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

/**
 * Validate that a given command must start with "lastb"
 */
public class CommandValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        if (value == null || value.length() == 0) {
            String msg = String.format("The command must not be null or empty");
            throw new ParameterException(msg);
        } else {
            String[] tokens = value.split(" ");
            String given = tokens[0];
            if (!given.equals("lastb")) {
                String msg = String.format("The [%s] command is not a valid start to a lastb command", value);
                throw new ParameterException(msg);
            }
        }
    }
}
