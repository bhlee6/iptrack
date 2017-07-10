package clitools;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileParameterValidator implements IParameterValidator {

    /**
     * Checks to make sure the given path exists and is a regular file
     * @param name name of parameter
     * @param value given value
     */
    @Override
    public void validate(String name, String value) throws ParameterException {
        Path path = Paths.get(value);
        if (!exists(path)) {
            String msg = String.format("[%s] does not exist: ",  value);
            throw new ParameterException(msg);
        }
        if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            String msg = String.format("[%s] is not a file: ", value);
            throw new ParameterException(msg);
        }
    }

    /**
     * Checks to make sure the supplied path exists
     * @param path User given path
     * @return True if the path leads to an existing file, false otherwise
     */
    private boolean exists(Path path) {
        return (Files.exists(path, LinkOption.NOFOLLOW_LINKS));
    }

}