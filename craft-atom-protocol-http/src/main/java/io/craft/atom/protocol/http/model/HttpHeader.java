package io.craft.atom.protocol.http.model;

import static io.craft.atom.protocol.http.HttpConstants.S_COLON;
import static io.craft.atom.protocol.http.HttpConstants.S_COMMA;
import static io.craft.atom.protocol.http.HttpConstants.S_CR;
import static io.craft.atom.protocol.http.HttpConstants.S_EQUAL_SIGN;
import static io.craft.atom.protocol.http.HttpConstants.S_LF;
import static io.craft.atom.protocol.http.HttpConstants.S_SEMICOLON;
import static io.craft.atom.protocol.http.HttpConstants.S_SP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a HTTP header field.
 * 
 * <p>
 * The HTTP header fields follow the same generic format as that given in
 * Section 3.1 of RFC 822. Each header field consists of a name followed by a
 * colon (":") and the field value. Field names are case-insensitive. The field
 * value MAY be preceded by any amount of LWS(Linear White Space), though a single SP is preferred.
 * 
 * <pre>
 *     LWS  = [CRLF] 1*( SP | HT )
 *     HT   = Horizontal Tab
 *     SP   = Space
 *     CRLF = Carriage return/Line feed
 * </pre>
 * 
 * <pre>
 *     message-header = field-name ":" [ field-value ]
 *     field-name     = token
 *     field-value    = *( field-content | LWS )
 *     field-content  = &lt;the OCTETs making up the field-value
 *                      and consisting of either *TEXT or combinations
 *                      of token, separators, and quoted-string&gt;
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpMessage
 */
@ToString(of = { "name", "value" })
public class HttpHeader implements Serializable {

	
	private static final long serialVersionUID = -689954816191532018L;

	
	@Getter @Setter private String name ;
	@Getter @Setter private String value;
	
	
	// ~ -----------------------------------------------------------------------------------------------------------

	
	public HttpHeader() {
		super();
	}

	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	
	public void appendValue(String valuePart) {
		if (value == null) {
			value = valuePart;
		} else {
			this.value += valuePart;
		}
	}
	
	/**
	 * Parses the header string value and return a list of {@code HttpHeaderValueElement}
	 * 
	 * @return a list of {@code HttpHeaderValueElement}
	 */
	public List<HttpHeaderValueElement> getValueElements() {
		List<HttpHeaderValueElement> elements = new ArrayList<HttpHeaderValueElement>();
		if (value == null || value.length() == 0) {
			return elements;
		}
		
		String[] earr = value.split(S_COMMA);
		for (String es : earr) {
			HttpHeaderValueElement hve = new HttpHeaderValueElement();
			String[] nvs = es.split(S_SEMICOLON);
			parseNameValue(hve, nvs[0]);
			for (int i = 1; i < nvs.length; i++) {
				parseParams(hve, nvs[i]);
			}
			elements.add(hve);
		}
		
		return elements;
	}
	
	private void parseParams(HttpHeaderValueElement hve, String pnv) {
		String[] nvpair = pnv.split(S_EQUAL_SIGN);
		if (nvpair.length > 1) {
			hve.addParam(nvpair[0], nvpair[1]);
		} else {
			hve.addParam(nvpair[0], null);
		}
	}
	
	private void parseNameValue(HttpHeaderValueElement hve, String nv) {
		String[] nvpair = nv.split(S_EQUAL_SIGN);
		hve.setName(nvpair[0]);
		if (nvpair.length > 1) {
			hve.setValue(nvpair[1]);
		}
	}
	
	public String toHttpString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append(S_COLON).append(S_SP).append(getValue()).append(S_CR).append(S_LF);
		return sb.toString();
	}
}
