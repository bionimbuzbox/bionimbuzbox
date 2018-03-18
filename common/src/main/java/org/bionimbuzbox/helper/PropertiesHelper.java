package org.bionimbuzbox.helper;

/**
 *  Copyright 2007 University Of Southern California
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.IOException;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesHelper
    extends Properties {

  /**
	 * 
	 */
	private static final long serialVersionUID = 8851269629056108825L;
	
/**
   * Adds new properties to an existing set of properties while
   * substituting variables. This function will allow value
   * substitutions based on other property values. Value substitutions
   * may not be nested. A value substitution will be ${property.key},
   * where the dollar-brace and close-brace are being stripped before
   * looking up the value to replace it with. Note that the ${..}
   * combination must be escaped from the shell.
   *
   * @param b is the set of properties to add to existing properties.
   * @return the combined set of properties.
   */
  protected Properties addProperties(Properties b) {
    // initial
    // Properties result = new Properties(this);
    Properties sys = System.getProperties();
    Pattern pattern = Pattern.compile("\\$\\{[-a-zA-Z0-9._]+\\}");

    for (Enumeration e = b.propertyNames(); e.hasMoreElements(); ) {
      String key = (String) e.nextElement();
      String value = b.getProperty(key);

      // unparse value ${prop.key} inside braces
      Matcher matcher = pattern.matcher(value);
      StringBuffer sb = new StringBuffer();
      while (matcher.find()) {
        // extract name of properties from braces
        String newKey = value.substring(matcher.start() + 2, matcher.end() - 1);

        // try to find a matching value in result properties
        String newVal = getProperty(newKey);

        // if still not found, try system properties
        if (newVal == null) {
          newVal = sys.getProperty(newKey);
        }

        // replace braced string with the actual value or empty string
        matcher.appendReplacement(sb, newVal == null ? "" : newVal);
      }
      matcher.appendTail(sb);
      setProperty(key, sb.toString());
    }
    return this;
  }

  /**
   * Adds new properties to an existing set of properties while
   * substituting variables. This function will allow value
   * substitutions based on other property values. Value substitutions
   * may not be nested. A value substitution will be ${property.key},
   * where the dollar-brace and close-brace are being stripped before
   * looking up the value to replace it with. Note that the ${..}
   * combination must be escaped from the shell.
   *
   * @param a is the initial set of known properties (besides System ones)
   * @param b is the set of properties to add to a
   * @return the combined set of properties from a and b.
   */
  protected static Properties addProperties(Properties a, Properties b) {
    // initial
    Properties result = new Properties(a);
    Properties sys = System.getProperties();
    Pattern pattern = Pattern.compile("\\$\\{[-a-zA-Z0-9._]+\\}");

    for (Enumeration e = b.propertyNames(); e.hasMoreElements(); ) {
      String key = (String) e.nextElement();
      String value = b.getProperty(key);

      // unparse value ${prop.key} inside braces
      Matcher matcher = pattern.matcher(value);
      StringBuffer sb = new StringBuffer();
      while (matcher.find()) {
        // extract name of properties from braces
        String newKey = value.substring(matcher.start() + 2, matcher.end() - 1);

        // try to find a matching value in result properties
        String newVal = result.getProperty(newKey);

        // if still not found, try system properties
        if (newVal == null) {
          newVal = sys.getProperty(newKey);
        }

        // replace braced string with the actual value or empty string
        matcher.appendReplacement(sb, newVal == null ? "" : newVal);
      }
      matcher.appendTail(sb);
      result.setProperty(key, sb.toString());
    }
    // final
    return result;
  }

  public PropertiesHelper() {
    super();
  }

  public PropertiesHelper(Properties defaults) {
    super(defaults);
  }

  protected PropertiesHelper(String propFilename, Properties defaults) throws
      IOException,
      MissingResourceException {
    // create empty new instance
    super(defaults);
  }

  /**
   * Accessor: Overwrite any properties from within the program.
   *
   * @param key is the key to look up
   * @param value is the new property value to place in the system.
   * @return the old value, or null if it didn't exist before.
   */
  /**  public Object setProperty(String key, String value) {
      return System.setProperty(key, value);
    }
   **/
  /**
   * Accessor: access to the internal properties as read from file.
   * An existing system property of the same key will have precedence
   * over any project property. This method will remove leading and
   * trailing ASCII control characters and whitespaces.
   *
   * @param key is the key to look up
   * @return the value for the key, or null, if not found.
   */
  /** public String getProperty(String key) {
     String result =
         System.getProperty(key, this.m_props.getProperty(key));
     return (result == null ? result : result.trim());
   }
   **/
  /**
   * Accessor: access to the internal properties as read from file
   * An existing system property of the same key will have precedence
   * over any project property. This method will remove leading and
   * trailing ASCII control characters and whitespaces.
   *
   * @param key is the key to look up
   * @param defValue is a default to use, if no value can be found for the key.
   * @return the value for the key, or the default value, if not found.
   */
  /** public String getProperty(String key, String defValue) {
     String result =
         System.getProperty(key, this.m_props.getProperty(key, defValue));
     return (result == null ? result : result.trim());
   }
   **/
  /**
   * Extracts a specific property key subset from the known properties.
   * The prefix may be removed from the keys in the resulting dictionary,
   * or it may be kept. In the latter case, exact matches on the prefix
   * will also be copied into the resulting dictionary.
   *
   * @param prefix is the key prefix to filter the properties by.
   * @param keepPrefix if true, the key prefix is kept in the resulting
   * dictionary. As side-effect, a key that matches the prefix exactly
   * will also be copied. If false, the resulting dictionary's keys are
   * shortened by the prefix. An exact prefix match will not be copied,
   * as it would result in an empty string key.
   * @return a property dictionary matching the filter key. May be
   * an empty dictionary, if no prefix matches were found.
   *
   * @see #getProperty( String ) is used to assemble matches
   */
  public Properties matchingSubset(String prefix, boolean keepPrefix) {
    Properties result = new Properties();

    // sanity check
    if (prefix == null || prefix.length() == 0) {
      return result;
    }

    String prefixMatch; // match prefix strings with this
    String prefixSelf; // match self with this
    if (prefix.charAt(prefix.length() - 1) != '.') {
      // prefix does not end in a dot
      prefixSelf = prefix;
      prefixMatch = prefix + '.';
    } else {
      // prefix does end in one dot, remove for exact matches
      prefixSelf = prefix.substring(0, prefix.length() - 1);
      prefixMatch = prefix;
    }
    // POSTCONDITION: prefixMatch and prefixSelf are initialized!

    // now add all matches into the resulting properties.
    // Remark 1: #propertyNames() will contain the System properties!
    // Remark 2: We need to give priority to System properties. This is done
    // automatically by calling this class's getProperty method.
    String key;
    for (Enumeration e = propertyNames(); e.hasMoreElements(); ) {
      key = (String) e.nextElement();

      if (keepPrefix) {
        // keep full prefix in result, also copy direct matches
        if (key.startsWith(prefixMatch) || key.equals(prefixSelf)) {
          result.setProperty(key,
                             getProperty(key));
        }
      } else {
        // remove full prefix in result, dont copy direct matches
        if (key.startsWith(prefixMatch)) {
          result.setProperty(key.substring(prefixMatch.length()),
                             getProperty(key));
        }
      }
    }

    // done
    return result;
  }

  /**
   * Extracts a specific property key subset from the properties passed.
   * The prefix may be removed from the keys in the resulting dictionary,
   * or it may be kept. In the latter case, exact matches on the prefix
   * will also be copied into the resulting dictionary.
   *
   *
   * @param prefix is the key prefix to filter the properties by.
   * @param keepPrefix if true, the key prefix is kept in the resulting
   * dictionary. As side-effect, a key that matches the prefix exactly
   * will also be copied. If false, the resulting dictionary's keys are
   * shortened by the prefix. An exact prefix match will not be copied,
   * as it would result in an empty string key.
   * @return a property dictionary matching the filter key. May be
   * an empty dictionary, if no prefix matches were found.
   *
   * @see #getProperty( String ) is used to assemble matches
   */
  public static Properties matchingSubset(Properties properties, String prefix,
                                          boolean keepPrefix) {
    Properties result = new Properties();

    // sanity check
    if (prefix == null || prefix.length() == 0) {
      return result;
    }

    String prefixMatch; // match prefix strings with this
    String prefixSelf; // match self with this
    if (prefix.charAt(prefix.length() - 1) != '.') {
      // prefix does not end in a dot
      prefixSelf = prefix;
      prefixMatch = prefix + '.';
    } else {
      // prefix does end in one dot, remove for exact matches
      prefixSelf = prefix.substring(0, prefix.length() - 1);
      prefixMatch = prefix;
    }
    // POSTCONDITION: prefixMatch and prefixSelf are initialized!

    // now add all matches into the resulting properties.
    // Remark 1: #propertyNames() will contain the System properties!
    // Remark 2: We need to give priority to System properties. This is done
    // automatically by calling this class's getProperty method.
    String key;
    for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
      key = (String) e.nextElement();

      if (keepPrefix) {
        // keep full prefix in result, also copy direct matches
        if (key.startsWith(prefixMatch) || key.equals(prefixSelf)) {
          result.setProperty(key,
                             properties.getProperty(key));
        }
      } else {
        // remove full prefix in result, dont copy direct matches
        if (key.startsWith(prefixMatch)) {
          result.setProperty(key.substring(prefixMatch.length()),
                             properties.getProperty(key));
        }
      }
    }

    // done
    return result;
  }
  
  public static void loadFilesIntoProperties(Properties properties) {   
    Set<String> propertyNames = properties.stringPropertyNames();
    
    for (String propertyName : propertyNames) {
      String propertyValue = properties.getProperty(propertyName);
      
      if (propertyName.endsWith("-file")) {
        propertyName = propertyName.replaceAll("-file", "");
        try {
          propertyValue = FileHelper.readFile(propertyValue);
        } catch (Exception e) {
          propertyValue = null;
        }
        properties.put(propertyName, propertyValue);
      }
    }
  }

}
