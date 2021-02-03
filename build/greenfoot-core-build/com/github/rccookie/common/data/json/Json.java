package com.github.rccookie.common.data.json;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.github.rccookie.common.data.Saveable;

public final class Json {
    private Json() {}



    public static final void validateName(String name) {
        if(name == null) throw new NullPointerException("Name can't be null");
        if(name.length() == 0) throw new RuntimeException("Name has to contain at least one character");
    }




    public static final String save(final Saveable object) {
        return save(object, Saveable.getSaveDir(object), object.getSaveName());
    }

    public static final String save(final Object object, final String path, final String filename) {
        if(object == null) return null;
        final File dir = new File(path);
        if(!dir.exists()) dir.mkdirs();
        final String jsonString = jsonString(object);
        final StringBuilder filePath = new StringBuilder(path);
        if(!filePath.toString().endsWith("\\")) filePath.append("\\");
        filePath.append(filename).append(".json");
        try {
            new Formatter(filePath.toString()).format("%s", jsonString).close();
            return jsonString;
        } catch (final Exception e) {
            System.out.println(e);
            return null;
        }
    }





    public static final String jsonString(Object object) {
        Class<?> objectClass = object.getClass();
        if(!objectClass.isAnnotationPresent(JsonSerializable.class)) throw new UnsupportedDataException(object);
        Map<String, Object> jsonMap = new JsonMap();
        for(Field field : objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            if(!field.isAnnotationPresent(JsonField.class)) continue;
            try{
                jsonMap.put(fieldName(field), field.get(object));
            } catch(IllegalAccessException e) { }
        }

        return '{' + jsonMap.entrySet().stream().map(valuePair -> {
            return '"' + valuePair.getKey() + '"' + ':' + stringFor(valuePair.getValue());
        }).collect(Collectors.joining(",")) + '}';
    }

    private static String fieldName(Field field) {
        JsonField j = field.getAnnotation(JsonField.class);
        if(j.value() == null || j.value().equals("")) return field.getName();
        return j.value();
    }

    private static String stringFor(Object object) {
        if(object == null) return null;
        if(object instanceof String) {
            StringBuilder string = new StringBuilder().append('"');
            StringBuilder valueString = new StringBuilder((String)object);
            for(int i=0; i<valueString.length(); i++) {
                if(valueString.charAt(i) == '\\' || valueString.charAt(i) == '\"') string.append("\\");
                string.append(valueString.charAt(i));
            }
            string.append('"');
            return string.toString();
        }
        if(object instanceof Object[]) {
            if(((Object[])object).length == 0) return "[]";
            StringBuilder string = new StringBuilder().append('[');
            for(final Object element : (Object[])object) string.append(stringFor(element)).append(',');
            string.deleteCharAt(string.length() - 1).append(']');
            return string.toString();
        }
        if(object instanceof Integer ||
           object instanceof Long ||
           object instanceof Short ||
           object instanceof Double ||
           object instanceof Float ||
           object instanceof Boolean) return object.toString();
        return jsonString(object);
    }






    /**
     * Loads the data of the loadable into the given loadable.
     * 
     * @param object The object to load the data into
     * @return False only if the object could not be loaded
     */
    @SuppressWarnings("unchecked")
    public static final <T> T load(Class<T> objectType, String dirPath, String fileName) {

        if(!objectType.isAnnotationPresent(JsonSerializable.class)) throw new IllegalArgumentException("Object is not json serializable");

        Constructor<T> constructor = null;
        for(Constructor<T> ctor : (Constructor<T>[])objectType.getDeclaredConstructors()) {
            ctor.setAccessible(true);
            if(!ctor.isAnnotationPresent(JsonConstructor.class)) continue;
            constructor = ctor;
            break;
        }
        if(constructor == null) throw new IllegalArgumentException("No json constructor found");

        final StringBuilder filePath = new StringBuilder(dirPath);
        if(!filePath.toString().endsWith("\\")) filePath.append("\\");
        filePath.append(fileName).append(".json");

        JsonMap saveData = read(filePath.toString());
        if(saveData == null) return null;

        Object[] dataArray = saveData.values().toArray();
        Class<?>[] paramTypes = constructor.getParameterTypes();
        for(int i=0; i<dataArray.length; i++) {
            dataArray[i] = castedValue(dataArray[i], paramTypes[i]);
        }

        try{
            return constructor.newInstance(dataArray);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Object castedValue(final Object value, final Class<?> cls) {
        if(cls.equals(int.class) || cls.equals(Integer.class)) return Long.valueOf((long)value).intValue();
        if(cls.equals(short.class) || cls.equals(Short.class)) return Long.valueOf((long)value).shortValue();
        if(cls.equals(float.class) || cls.equals(Float.class)) return Double.valueOf((long)value).floatValue();
        return value;
    }

    /**
     * Reads the given json file into a json object.
     * 
     * @param filePath The file to read
     * @return The object represented in the given file, or {@code null}, if the file could not be read.
     */
    public static final synchronized JsonMap read(String filePath) {
        try{
            final String fileText = Files.readString(Path.of(filePath));
            //if(fileText.length() == 0) System.err.println("File is empty");
            return readNextObject(fileText);
        }
        catch(Exception e) {
            //System.err.println("An error occured during loading");
            //e.printStackTrace();
            return null;
        }
    }


    /**
     * Returns the next full object of the given json-formatted string.
     * 
     * @param jsonFileText A string containing at least the for the object relevant part of the json
     * @return The json object
     * @throws NoSuchElementException If the given object could not be read.
     */
    public static final JsonMap readNextObject(String jsonFileText) {
        return readNextObject(new StringBuilder(jsonFileText.replaceAll("[\\n\\t\\r ]", "")));
    }



    private static final void readAndAddNextPair(JsonMap object, StringBuilder remaining) {
        // get the variable name
        String varName = readNextString(remaining);

        // remove the colomn (:)
        remaining.deleteCharAt(0);

        // get the variable value
        Object value = readNextValue(remaining);

        // write pair into json object
        object.put(varName, value);
    }



    private static final boolean isNumChar(char c) {
        return (
            c == '0' ||
            c == '1' ||
            c == '2' ||
            c == '3' ||
            c == '4' ||
            c == '5' ||
            c == '6' ||
            c == '7' ||
            c == '8' ||
            c == '9' ||
            c == '-' ||
            c == '.' ||
            c == 'E'
        );
    }




    private static final Object readNextValue(StringBuilder remaining) {
        char start = remaining.charAt(0);

        if(start == '"') return readNextString(remaining);
        if(start == '[') return readNextArray(remaining);
        if(start == '{') return readNextObject(remaining);

        if(isNumChar(start)) return readNextNumber(remaining);

        if(start == 't') return readNextTrue(remaining);
        if(start == 'f') return readNextFalse(remaining);
        if(start == 'n') return readNextNull(remaining);

        throw new NoSuchElementException();
    }


    private static final JsonMap readNextObject(StringBuilder remaining) {
        final JsonMap object = new JsonMap();

        // Delete opening curly bracket ({)
        remaining.deleteCharAt(0);

        while(remaining.charAt(0) != '}') {

            readAndAddNextPair(object, remaining);

            // Delete the next comma, or end the object
            if(remaining.charAt(0) == ',') remaining.deleteCharAt(0);
            else break;
        }

        // Delete the opening curly bracket (})
        remaining.deleteCharAt(0);

        // Return the json object
        return object;
    }


    private static final Object[] readNextArray(StringBuilder remaining) {
        // Create a list to store the array's objects in
        final ArrayList<Object> array = new ArrayList<>();

        // Delete the opening bracket ([)
        remaining.deleteCharAt(0);

        // Add more objects if the array is not closing
        while(remaining.charAt(0) != ']') {

            // Read out the next value
            array.add(readNextValue(remaining));
            
            // Delete the next comma, or end the array
            if(remaining.charAt(0) == ',') remaining.deleteCharAt(0);
            else break;
        }

        // Delete the closing bracket (])
        remaining.deleteCharAt(0);

        // Return the list as an array
        return array.toArray();
    }


    private static final String readNextString(StringBuilder remaining) {
        StringBuilder string = new StringBuilder();
        remaining.deleteCharAt(0);
        while(remaining.charAt(0) != '"') {
            char next = remaining.charAt(0);
            remaining.deleteCharAt(0);
            if(next == '\\') {
                string.append(remaining.charAt(0));
                remaining.deleteCharAt(0);
            }
            else string.append(next);
        }
        remaining.deleteCharAt(0);
        return string.toString();
    }


    private static final Number readNextNumber(StringBuilder remaining) {
        StringBuilder number = new StringBuilder();
        while(isNumChar(remaining.charAt(0))) {
            number.append(remaining.charAt(0));
            remaining.deleteCharAt(0);
        }
        final String numberString = number.toString();
        try{
            if(numberString.contains(".")) return Double.parseDouble(numberString);
            return Long.parseLong(numberString);
        }
        catch(Exception e) {
            throw new NoSuchElementException();
        }
    }


    private static final boolean readNextTrue(StringBuilder remaining) {
        if(remaining.substring(0, 4).equals("true")) {
            remaining.delete(0, 4);
            return true;
        }
        throw new NoSuchElementException();
    }

    private static final boolean readNextFalse(StringBuilder remaining) {
        if(remaining.substring(0, 5).equals("false")) {
            remaining.delete(0, 5);
            return false;
        }
        throw new NoSuchElementException();
    }

    private static final Object readNextNull(StringBuilder remaining) {
        if(remaining.substring(0, 4).equals("null")) {
            remaining.delete(0, 4);
            return null;
        }
        throw new NoSuchElementException();
    }
}
