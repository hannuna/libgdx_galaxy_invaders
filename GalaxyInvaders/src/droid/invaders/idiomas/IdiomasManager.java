package droid.invaders.idiomas;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class IdiomasManager extends Control {

	private Locale local;
	private String idioma;
	ResourceBundle cadenas;

	public IdiomasManager() {
		idioma = Locale.getDefault().getLanguage();
		local = new Locale(idioma);
		cadenas = ResourceBundle.getBundle("strings", local, new GdxFileControl("ISO-8859-1", FileType.Internal));
	}

	public String getTraduccion(String nombre) {

		return cadenas.getString(nombre);
	}

	/**
	 * 
	 * @param idioma
	 *            Idioma a comprar por ejemplo "es" (espanol), "en" (ingles)
	 * @return verdadero o falso si el idioma actual coincide con el idioma introducido como parametro
	 */
	public boolean idiomaActualEs(String idioma) {
		return idioma.equals(this.idioma);
	}

	/**
	 * Regresa el idioma actual por ejemplo "es" para espanol, "en" para ingles
	 * 
	 * @return idioma actual
	 */
	public String getIdiomaActual() {
		return idioma;
	}

	public class GdxFileControl extends Control {

		private String encoding;
		private FileType fileType;

		public GdxFileControl(String encoding, FileType fileType) {
			this.encoding = encoding;
			this.fileType = fileType;
		}

		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {

			// The below is a copy of the default implementation.
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");
			ResourceBundle bundle = null;
			FileHandle fileHandle = Gdx.files.getFileHandle("strings/" + resourceName, fileType);
			if (fileHandle.exists()) {
				InputStream stream = null;
				try {
					stream = fileHandle.read();
					// Only this line is changed to make it to read properties files as UTF-8.
					bundle = new PropertyResourceBundle(new InputStreamReader(stream, encoding));
				}
				finally {
					if (stream != null)
						stream.close();
				}
			}
			return bundle;
		}
	}

}
