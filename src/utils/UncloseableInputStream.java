package utils;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * Класс-обёртка InputStream, позволяющий использовать исходный поток без риска
 * его закрытия, что может быть необходимо при использовании потока,
 * предоставляемого и управляемого внешним сервисом.
 */
public final class UncloseableInputStream extends FilterInputStream {

	private UncloseableInputStream(InputStream in) {
		super(in);
	}

	@Override
	public void close() {
		// do nothing
	}

	public static InputStream wrap(InputStream inputStream) {
		return new UncloseableInputStream(inputStream);
	}
}
