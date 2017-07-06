package clitools;

import com.beust.jcommander.IStringConverter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter implements IStringConverter<Path> {
    /**
     * Convert the given string value to a path
     * @param value
     * @return
     */
    @Override
    public Path convert(String value) {
        return  Paths.get(value);
    }
}