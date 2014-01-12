/*
 ===========================================================================
 Copyright (c) 2010 BrickRed Technologies Limited

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===========================================================================

 */
package org.brickred.socialauth.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.SessionProperties;

/**
 * Stores the keys and secret for OAuth access token as well as OAuth request
 * token.
 * 
 * @author tarunn@brickred.com
 * 
 */
public class AccessGrant implements Serializable, SessionProperties {

	private static final long serialVersionUID = -7120362372191191930L;
	private String key;
	private String secret;
	private String providerId;
	private Map<String, Object> _attributes;
	private Permission permission;

	/**
	 * 
	 * @param key
	 *            the Key
	 * @param secret
	 *            the Secret
	 */
	public AccessGrant(final String key, final String secret) {
		this.key = key;
		this.secret = secret;
	}

	public AccessGrant() {
	}

	/**
	 * Retrieves the Token Key
	 * 
	 * @return the Token Key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Updates the Token Key
	 * 
	 * @param key
	 *            the Token Key
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Retrieves the Token Secret
	 * 
	 * @return the Token Secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * Updates the Token Secret
	 * 
	 * @param secret
	 *            the Token Secret
	 */
	public void setSecret(final String secret) {
		this.secret = secret;
	}

	/**
	 * Gets the attributes of this token.
	 */
	public Map<String, Object> getAttributes() {
		return _attributes;
	}

	/**
	 * Gets an attribute based from the given key.
	 */
	public Object getAttribute(final String key) {
		return _attributes == null ? null : _attributes.get(key);
	}

	/**
	 * Sets an attribute based from the given key and value.
	 */
	public void setAttribute(final String key, final Object value) {
		if (_attributes == null) {
			_attributes = new HashMap<String, Object>();
		}

		_attributes.put(key, value);
	}

	/**
	 * Sets an attributes from given attributes map.
	 */
	public void setAttributes(final Map<String, Object> attributes) {
		if (_attributes == null) {
			_attributes = new HashMap<String, Object>();
		}

		_attributes.putAll(attributes);
	}

	/**
	 * Retrieves the provider id.
	 * 
	 * @return the provider id.
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * Updates the provider id.
	 * 
	 * @param providerId
	 *            the provider id.
	 */
	public void setProviderId(final String providerId) {
		this.providerId = providerId;
	}

	/**
	 * Retrieves the scope
	 * 
	 * @return the scope
	 */
	public Permission getPermission() {
		return permission;
	}

	/**
	 * 
	 * @param permission
	 *            Permission object which can be Permission.AUHTHENTICATE_ONLY,
	 *            Permission.ALL, Permission.DEFAULT
	 */
	public void setPermission(final Permission permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");
		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" token key : " + key + NEW_LINE);
		result.append(" token secret : " + secret + NEW_LINE);
		result.append("provider id : " + providerId + NEW_LINE);
		result.append("permission : " + permission + NEW_LINE);
		if (_attributes != null) {
			result.append(_attributes.toString());
		}
		result.append("}");

		return result.toString();
	}

    @Override
    public void read(String ns, Properties p) {
        key = p.getProperty(Prefix.withNs(ns, "key"));
        secret = p.getProperty(Prefix.withNs(ns, "secret"));
        String attrs = p.getProperty(Prefix.withNs(ns, "attrs"));
        if(attrs != null) {
            for(String attr : Splitter.on(',').split(attrs)) {
                Object val;
                String strVal = p.getProperty(Prefix.withNs(ns, attr));
                if(strVal.startsWith("boolean:")) {
                    val = new Boolean(strVal.substring("boolean:".length()));
                } else if(strVal.startsWith("long:")) {
                    val = new Long(strVal.substring("long:".length()));
                } else if(strVal.startsWith("int:")) {
                    val = new Integer(strVal.substring("int:".length()));
                } else {
                    val = strVal;
                }

                setAttribute(attr, val);
            }
        }
    }

    @Override
    public void write(String ns, Properties p) {
        p.setProperty(Prefix.withNs(ns, "instance"), "yes");
        if(key != null) {
            p.setProperty(Prefix.withNs(ns, "key"), key);
        }
        if(secret != null) {
            p.setProperty(Prefix.withNs(ns, "secret"), key);
        }
        if(_attributes != null) {
            p.setProperty(Prefix.withNs(ns, "attrs"), Joiner.on(',').join(_attributes.keySet()));
            for(Map.Entry<String, Object> e : _attributes.entrySet()) {
                String strVal;
                Object val = e.getValue();
                if(val instanceof Boolean) {
                    strVal = "boolean:";
                } else if(val instanceof Long) {
                    strVal = "long:";
                } else if(val instanceof Integer) {
                    strVal = "int:";
                } else {
                    strVal = "";
                }
                strVal += String.valueOf(val);
                p.setProperty(Prefix.withNs(ns, e.getKey()), strVal);
            }
        }
    }

    public static AccessGrant fromProperties(String ns, Properties p) {
        String instance = p.getProperty(Prefix.withNs(ns, "instance"));
        if(instance == null)
            return null;
        AccessGrant grant = new AccessGrant();
        grant.read(ns, p);
        if(grant.key != null)
            return grant;
        return null;
    }
}
