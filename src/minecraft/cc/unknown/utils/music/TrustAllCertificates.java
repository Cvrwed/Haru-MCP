package cc.unknown.utils.music;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

final class TrustAllCertificates implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}