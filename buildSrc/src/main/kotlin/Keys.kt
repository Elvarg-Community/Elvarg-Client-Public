import org.bouncycastle.jce.X509Principal
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.x509.X509V3CertificateGenerator
import org.gradle.internal.impldep.org.joda.time.DateTime
import java.io.File
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.cert.X509Certificate
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs


/**
 * Simple script for generating keys and certificates as needed to connect to the ING API.
 * It generates a key and certificate for signing, and a separate one for the TLS connection.
 *
 * Note that I have avoided any third party dependency in this script. It is purely based on Java standard libraries.
 *
 * This is the equivalent of the following openssl command:
 * openssl req -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
 */

fun main() {
    Keys.generateKeys()
}

object Keys {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun generateKeys() {
        println("Creating keys and certificates for Signing and TLS connection..")

        val privateFile = File("key-private.pem")
        val keyFile = File("key-public.pem")
        val certFile = File("launcher.crt")
        val keyPair = generateKeyPair()
        val certificate = createSelfSignedCertificate(keyPair)

        privateFile.writeText(privateKeyToPem(keyPair.private))
        println("Created ${privateFile.absoluteFile}")

        keyFile.writeText(publicKeyToPem(keyPair.public))
        println("Created ${keyFile.absoluteFile}")

        certFile.writeText(certificateToPem(certificate))
        println("Created ${certFile.absoluteFile}")

    }

    private fun generateKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        return kpg.genKeyPair()
    }

    // transform to base64 with begin and end line
    private fun publicKeyToPem(publicKey: PublicKey): String {
        val base64PubKey = Base64.getEncoder().encodeToString(publicKey.encoded)
        return "-----BEGIN PUBLIC KEY-----\n" + base64PubKey.replace("(.{64})".toRegex(), "$1\n") + "\n-----END PUBLIC KEY-----\n"
    }

    private fun privateKeyToPem(privateKey: PrivateKey): String {
        val base64PubKey = Base64.getEncoder().encodeToString(privateKey.encoded)
        return "-----BEGIN PRIVATE KEY-----\n" + base64PubKey.replace("(.{64})".toRegex(), "$1\n") + "\n-----END PRIVATE KEY-----\n"
    }

    private fun certificateToPem(certificate: X509Certificate): String {
        val base64PubKey = Base64.getEncoder().encodeToString(certificate.encoded)

        return "-----BEGIN CERTIFICATE-----\n" + base64PubKey.replace("(.{64})".toRegex(), "$1\n") + "\n-----END CERTIFICATE-----\n"
    }

    private fun createSelfSignedCertificate(
        privateKey: KeyPair
    ): X509Certificate {
        val certGenerator = X509V3CertificateGenerator()
        certGenerator.setSerialNumber(BigInteger.valueOf(abs(Random().nextLong())))
        val notBefore = DateTime.now()
        val notAfter = notBefore.plusYears(900).toDate()

        certGenerator.setIssuerDN(X509Principal("CN=localhost")); //same since it is self-signed
        certGenerator.setSubjectDN(X509Principal("CN=localhost")); //same since it is self-signed
        certGenerator.setIssuerDN(X509Principal("CN=localhost")); //same since it is self-signed
        certGenerator.setNotBefore(notBefore.toDate())
        certGenerator.setNotAfter(notAfter)
        certGenerator.setPublicKey(privateKey.public)
        certGenerator.setSignatureAlgorithm("SHA256withRSA")
        return certGenerator.generate(privateKey.private, "BC")
    }

    fun sha256(key : File,bootstrapFile : File, output : File) {

        val buff: ByteArray = bootstrapFile.readBytes()

        val signature1 = Signature.getInstance("SHA256withRSA")
        val privateKey: PrivateKey = get(key)!!
        signature1.initSign(privateKey)
        signature1.update(buff)
        val signature = signature1.sign()

        output.writeBytes(signature)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun get(filename: File): PrivateKey? {


        val pem = String(filename.readBytes(), StandardCharsets.ISO_8859_1)
        val parse: Pattern = Pattern.compile("(?m)(?s)^---*BEGIN.*---*$(.*)^---*END.*---*$.*")
        val encoded: String = parse.matcher(pem).replaceFirst("$1")

        val kf = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(encoded))
        return kf.generatePrivate(keySpec)
    }


}