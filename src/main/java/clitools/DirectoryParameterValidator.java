package clitools;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryParameterValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        Path path = Paths.get(value);
        if (!exists(path)) {
            String msg = String.format("The [%s] directory [%s] does not exist: ", name, value);
            throw new ParameterException(msg);
        }
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            String msg = String.format("The specified directory [%s] is not a directory: ", value);
            throw new ParameterException(msg);
        }
        if (!checkPermissions(path)) {
            String msg = String.format("Application does not have read and write permission for [%s] directory [%s]", name, value);
            throw new ParameterException(msg);
        }

    }

    private boolean checkPermissions(Path path) {
        return (Files.isReadable(path) && Files.isWritable(path));
    }

    private boolean exists(Path path) {
        return (Files.exists(path, LinkOption.NOFOLLOW_LINKS));
    }

}