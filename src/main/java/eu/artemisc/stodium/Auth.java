package eu.artemisc.stodium;

import android.support.annotation.NonNull;

import org.abstractj.kalium.Sodium;

/**
 * Auth wraps calls to crypto_auth, based on HMAC-SHA512256
 *
 * @author Jan van de Molengraft [jan@artemisc.eu]
 */
public class Auth {
    static {
        // Require sodium_init();
        Stodium.StodiumInit();
    }

    // block the constructor
    private Auth() {}

    // constants
    public static final int BYTES = 32;
    public static final int KEYBYTES = 32;

    public static final String PRIMITIVE = Sodium.crypto_auth_primitive();

    // wrappers

    /**
     *
     * @param dstOut
     * @param srcIn
     * @param srcKey
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public static void auth(@NonNull final byte[] dstOut,
                            @NonNull final byte[] srcIn,
                            @NonNull final byte[] srcKey)
            throws StodiumException {
        Stodium.checkSize(dstOut.length, BYTES, "Auth.BYTES");
        Stodium.checkSize(srcKey.length, KEYBYTES, "Auth.KEYBYTES");
        Stodium.checkStatus(
                Sodium.crypto_auth(dstOut, srcIn, srcIn.length, srcKey));
    }

    /**
     *
     * @param srcTag
     * @param srcIn
     * @param srcKey
     * @return
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public static boolean authVerify(@NonNull final byte[] srcTag,
                                     @NonNull final byte[] srcIn,
                                     @NonNull final byte[] srcKey)
            throws StodiumException {
        Stodium.checkSize(srcTag.length, BYTES, "Auth.BYTES");
        Stodium.checkSize(srcKey.length, KEYBYTES, "Auth.KEYBYTES");
        return Sodium.crypto_auth_verify(
                srcTag, srcIn, srcIn.length, srcKey) == 0;
    }
}
