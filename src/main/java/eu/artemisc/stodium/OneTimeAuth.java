package eu.artemisc.stodium;

import android.support.annotation.NonNull;

import org.abstractj.kalium.Sodium;

import java.util.Arrays;

/**
 * OneTimeAuth wraps calls to crypto_onetimeauth, a message authentication code
 * based on Poly1305.
 *
 * @author Jan van de Molengraft [jan@artemisc.eu]
 */
public class OneTimeAuth {
    static {
        // Require sodium_init();
        Stodium.StodiumInit();
    }

    // constants
    public static final int BYTES = Sodium.crypto_onetimeauth_bytes();
    public static final int KEYBYTES = Sodium.crypto_onetimeauth_keybytes();

    public static final int STATEBYTES = Sodium.crypto_onetimeauth_statebytes();

    public static final String PRIMITIVE = Sodium.crypto_onetimeauth_primitive();

    // Implementation of the stream API

    /**
     * state holds the binary representation of the crypto_onetimeauth_state
     * value.
     */
    @NonNull
    private final byte[] state;

    /**
     * State allocates a byte array that holds the raw packed value of the C
     * crypto_onetimeauth_state bytes.
     */
    public OneTimeAuth() {
        this.state = new byte[STATEBYTES];
    }

    /**
     * Poly1305 constructor that automatically calls {@link #init(byte[])} with
     * the provided key.
     *
     * @param key
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public OneTimeAuth(@NonNull final byte[] key)
            throws StodiumException {
        this();
        init(key);
    }

    /**
     * State copy-constructor. If _finish should be called on multiple
     * occasions during the streaming without losing the state, it can be
     * copied.
     *
     * @param original The original State that should be copied
     */
    public OneTimeAuth(@NonNull final OneTimeAuth original) {
        this.state = Arrays.copyOf(original.state, original.state.length);
    }

    /**
     *
     * @param key
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public void init(@NonNull final byte[] key)
            throws StodiumException {
        Stodium.checkSize(key.length, KEYBYTES, "OneTimeAuth.KEYBYTES");
        Stodium.checkStatus(
                Sodium.crypto_onetimeauth_init(state, key));
    }

    /**
     *
     * @param in
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public void update(@NonNull final byte[] in)
            throws StodiumException {
        update(in, 0, in.length);
    }

    /**
     *
     * @param in
     * @param offset
     * @param length
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public void update(@NonNull final byte[] in,
                       final int offset,
                       final int length)
            throws StodiumException {
        Stodium.checkOffsetParams(in.length, offset, length);
        Stodium.checkStatus(Sodium.crypto_onetimeauth_update_offset(
                state, in, offset, length));
    }

    /**
     * equivalent to calling {@link #doFinal(byte[], int)} with
     * {@code doFinal(out, 0)}.
     *
     * @param out
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public void doFinal(@NonNull final byte[] out)
            throws StodiumException {
        doFinal(out, 0);
    }

    /**
     *
     * @param out
     * @param offset
     * @throws ConstraintViolationException
     * @throws StodiumException
     */
    public void doFinal(@NonNull final byte[] out,
                        final int offset)
            throws StodiumException {
        Stodium.checkOffsetParams(out.length, offset, BYTES);
        Stodium.checkStatus(Sodium.crypto_onetimeauth_final_offset(
                state, out, offset));
    }

    // wrappers

    //
    // non-stream methods
    //

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
        final OneTimeAuth auth = new OneTimeAuth(srcKey);
        auth.update(srcIn);
        auth.doFinal(dstOut);
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
        final byte[] verify = new byte[BYTES];
        auth(verify, srcIn, srcKey);
        return Stodium.isEqual(srcTag, verify);
    }
}
