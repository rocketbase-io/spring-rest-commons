package io.rocketbase.commons.resource;

import io.rocketbase.commons.util.QueryParamBuilder;
import io.rocketbase.commons.util.UrlParts;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

public interface BaseRestResource {

    default UriComponentsBuilder appendParams(UriComponentsBuilder uriBuilder, Pageable pageable) {
        return QueryParamBuilder.appendParams(uriBuilder, pageable);
    }

    /**
     * create HttpHeaders with ACCEPT_LANGUAGE key of given {@link LocaleContextHolder}
     *
     * @return Headers with language key
     */
    default HttpHeaders createHeaderWithLanguage() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT_LANGUAGE,
                LocaleContextHolder.getLocale()
                        .getLanguage());
        return headers;
    }

    /**
     * checks if given uri ends with slash or adds it if missing
     *
     * @param uri given url
     * @return url with / at the end
     */
    default String ensureEndsWithSlash(String uri) {
        return UrlParts.ensureEndsWithSlash(uri);
    }

    /**
     * checks if given uri starts with slash or adds it if missing
     *
     * @param uri given url
     * @return url with / at the beginning
     */
    default String ensureStartsWithSlash(String uri) {
        return UrlParts.ensureEndsWithSlash(uri);
    }

    /**
     * checks if given uri ends with slash or adds it if missing
     *
     * @param uri given path of url
     * @return path with / at beginning + end
     */
    default String ensureStartsAndEndsWithSlash(String uri) {
        return UrlParts.ensureStartsAndEndsWithSlash(uri);
    }

    /**
     * instantiate an UriComponentsBuilder from given url and ensures ends with slash
     *
     * @param baseApiUrl base url
     * @return UriComponentsBuilder
     */
    default UriComponentsBuilder createUriComponentsBuilder(String baseApiUrl) {
        return UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseApiUrl));
    }
}
