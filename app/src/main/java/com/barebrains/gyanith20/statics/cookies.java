package com.barebrains.gyanith20.statics;

import android.util.Log;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class cookies implements CookieStore {


    public static cookies instance;

    public static cookies getInstance(){
        if (instance == null)
            instance = new cookies();
        return instance;
    }

    private cookies(){
        cookieMap = new HashMap<>();
    }

    private Map<URI, Set<HttpCookie>> cookieMap;

    @Override
    public void add(URI uri, HttpCookie cookie) {
        uri = cookieUri(uri, cookie);

        Set<HttpCookie> targetCookies = cookieMap.get(uri);
        if (targetCookies == null) {
            targetCookies = new HashSet<>();
            cookieMap.put(uri, targetCookies);
        }
        targetCookies.remove(cookie);
        targetCookies.add(cookie);
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        Log.d("asd","get : " + uri);
        return getValidCookies(uri);
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        Log.d("asd","getCookies");
        List<HttpCookie> allValidCookies = new ArrayList<HttpCookie>();
        for (URI storedUri : cookieMap.keySet()) {
            allValidCookies.addAll(getValidCookies(storedUri));
        }

        return allValidCookies;
    }

    @Override
    public synchronized List<URI> getURIs() {
        return new ArrayList<>(cookieMap.keySet());
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        Set<HttpCookie> targetCookies = cookieMap.get(uri);
        return targetCookies != null && targetCookies
                .remove(cookie);

    }

    @Override
    public boolean removeAll() {
        cookieMap.clear();
        return true;
    }

    private static URI cookieUri(URI uri, HttpCookie cookie) {
        URI cookieUri = uri;
        if (cookie.getDomain() != null) {
            // Remove the starting dot character of the domain, if exists (e.g: .domain.com -> domain.com)
            String domain = cookie.getDomain();
            if (domain.charAt(0) == '.') {
                domain = domain.substring(1);
            }
            try {
                cookieUri = new URI(uri.getScheme() == null ? "http"
                        : uri.getScheme(), domain,
                        cookie.getPath() == null ? "/" : cookie.getPath(), null);
            } catch (URISyntaxException e) {
                Log.w("asd", "cookieUri : " + e);
            }
        }
        return cookieUri;
    }

    private List<HttpCookie> getValidCookies(URI uri) {
        List<HttpCookie> targetCookies = new ArrayList<HttpCookie>();
        // If the stored URI does not have a path then it must match any URI in
        // the same domain
        for (URI storedUri : cookieMap.keySet()) {
            // Check ith the domains match according to RFC 6265
            if (checkDomainsMatch(storedUri.getHost(), uri.getHost())) {
                // Check if the paths match according to RFC 6265
                if (checkPathsMatch(storedUri.getPath(), uri.getPath())) {
                    targetCookies.addAll(cookieMap.get(storedUri));
                }
            }
        }
        return targetCookies;
    }

    /* http://tools.ietf.org/html/rfc6265#section-5.1.3
    A string domain-matches a given domain string if at least one of the
    following conditions hold:
    o  The domain string and the string are identical.  (Note that both
    the domain string and the string will have been canonicalized to
    lower case at this point.)
    o  All of the following conditions hold:
        *  The domain string is a suffix of the string.
        *  The last character of the string that is not included in the
           domain string is a %x2E (".") character.
        *  The string is a host name (i.e., not an IP address). */

    private boolean checkDomainsMatch(String cookieHost, String requestHost) {
        return requestHost.equals(cookieHost) || requestHost.endsWith("." + cookieHost);
    }

    /*  http://tools.ietf.org/html/rfc6265#section-5.1.4
        A request-path path-matches a given cookie-path if at least one of
        the following conditions holds:
        o  The cookie-path and the request-path are identical.
        o  The cookie-path is a prefix of the request-path, and the last
        character of the cookie-path is %x2F ("/").
        o  The cookie-path is a prefix of the request-path, and the first
        character of the request-path that is not included in the cookie-
        path is a %x2F ("/") character. */

    private boolean checkPathsMatch(String cookiePath, String requestPath) {
        return requestPath.equals(cookiePath) ||
                (requestPath.startsWith(cookiePath) && cookiePath.charAt(cookiePath.length() - 1) == '/') ||
                (requestPath.startsWith(cookiePath) && requestPath.substring(cookiePath.length()).charAt(0) == '/');
    }


}
