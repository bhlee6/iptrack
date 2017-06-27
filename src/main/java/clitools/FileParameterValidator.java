package clitools;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileParameterValidator implements IParameterValidator {

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

    private boolean exists(Path path) {
        return (Files.exists(path, LinkOption.NOFOLLOW_LINKS));
    }

}