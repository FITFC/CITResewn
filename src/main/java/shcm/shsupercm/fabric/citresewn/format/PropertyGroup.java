package shcm.shsupercm.fabric.citresewn.format;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class PropertyGroup {
    public final Map<PropertyKey, Set<PropertyValue>> properties = new LinkedHashMap<>();
    public final Identifier identifier;

    protected PropertyGroup(Identifier identifier) {
        this.identifier = identifier;
    }

    public abstract String getExtension();

    public abstract PropertyGroup load(InputStream is) throws IOException, InvalidIdentifierException;

    protected void put(int position, String key, String keyMetadata, String delimiter, String value) throws InvalidIdentifierException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        this.properties.computeIfAbsent(PropertyKey.of(key), id -> new LinkedHashSet<>()).add(new PropertyValue(keyMetadata, value, delimiter, position));
    }

    public Set<PropertyValue> get(String namespace, String... pathAliases) {
        Set<PropertyValue> values = new LinkedHashSet<>();

        for (String path : pathAliases) {
            Set<PropertyValue> possibleValues = this.properties.get(new PropertyKey(namespace, path));
            if (possibleValues != null)
                values.addAll(possibleValues);
        }

        return values;
    }

    public static PropertyGroup tryParseGroup(Identifier identifier, InputStream is) throws IOException {
        PropertyGroup group;
        if (identifier.getPath().endsWith(PropertiesGroupAdapter.EXTENSION))
            group = new PropertiesGroupAdapter(identifier);
        else
            return null;

        group.load(is);

        return group;
    }
}
