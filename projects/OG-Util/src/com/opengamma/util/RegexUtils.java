/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Utility methods to simplify comparisons.
 * <p>
 * This is a static thread-safe utility class.
 */
public final class RegexUtils {

  /**
   * Restricted constructor.
   */
  private RegexUtils() {
  }

  //-------------------------------------------------------------------------
  /**
   * Converts a simple wildcard style pattern to a regex pattern.
   * <p>
   * The asterisk (<code>*</code>) matches zero or more characters.<br />
   * The question mark (<code>?</code>) matches one character.<br />
   * <p>
   * The returned pattern will be setup to match a whole string using
   * <code>^</code> and <code>$</code>.
   * 
   * @param text  the text to match, not null
   * @return the pattern, not null
   */
  public static Pattern wildcardsToPattern(final String text) {
    ArgumentChecker.notNull(text, "text");
    StringTokenizer tkn = new StringTokenizer(text, "?*", true);
    StringBuilder buf = new StringBuilder(text.length() + 10);
    buf.append('^');
    boolean lastStar = false;
    while (tkn.hasMoreTokens()) {
      String str = tkn.nextToken();
      if (str.equals("?")) {
        buf.append('.');
        lastStar = false;
      } else if (str.equals("*")) {
        if (lastStar == false) {
          buf.append(".*");
        }
        lastStar = true;
      } else {
        buf.append(Pattern.quote(str));
        lastStar = false;
      }
    }
    buf.append('$');
    return Pattern.compile(buf.toString(), Pattern.CASE_INSENSITIVE);
  }

  /**
   * Checks if a string matches a potentially wildcard string.
   * <p>
   * The asterisk (<code>*</code>) matches zero or more characters.<br />
   * The question mark (<code>?</code>) matches one character.<br />
   * 
   * @param searchCriteriaWithWildcard  the search criteria text with wildcards, null returns false
   * @param textToMatchAgainst  the text without wildcards to match against, null returns false
   * @return true if the text
   */
  public static boolean wildcardMatch(final String searchCriteriaWithWildcard, final String textToMatchAgainst) {
    if (searchCriteriaWithWildcard == null || textToMatchAgainst == null) {
      return false;
    }
    return wildcardsToPattern(searchCriteriaWithWildcard).matcher(textToMatchAgainst).matches();
  }

}
