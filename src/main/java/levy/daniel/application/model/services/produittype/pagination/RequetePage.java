/* ********************************************************************* */
/* *************************** PAGINATION ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE RequetePage.java :</p>
 * <p>Requête de pagination neutre (indépendante de Spring).</p>
 * </div>
 *
 * <p>
 * Convention : pageNumber 0-based.
 * </p>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 19 janvier 2026
 */
public class RequetePage implements Serializable {

	// *************************** CONSTANTES ******************************/

	/** Serial UID. */
	private static final long serialVersionUID = 1L;

	/** Numéro de page par défaut (base 0). */
	public static final int PAGE_DEFAUT = 0;

	/** Taille de page par défaut. */
	public static final int TAILLE_DEFAUT = 20;

	/** Taille minimale. */
	public static final int TAILLE_MIN = 1;


	// *************************** ATTRIBUTS *******************************/

	/** Numéro de page (base 0). */
	private int pageNumber;

	/** Taille de page (>= 1). */
	private int pageSize;

	/** Liste de tris (peut être vide). */
	private List<TriSpec> tris;


	// ************************* METHODES **********************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public RequetePage() {
		this(PAGE_DEFAUT, TAILLE_DEFAUT, null);
	}

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pPageNumber : int
	 * @param pPageSize : int
	 */
	public RequetePage(final int pPageNumber, final int pPageSize) {
		this(pPageNumber, pPageSize, null);
	}

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 3.</p>
	 * </div>
	 *
	 * @param pPageNumber : int
	 * @param pPageSize : int
	 * @param pTris : List&lt;TriSpec&gt;
	 */
	public RequetePage(
			final int pPageNumber,
			final int pPageSize,
			final List<TriSpec> pTris) {

		super();

		this.pageNumber = (pPageNumber >= 0) ? pPageNumber : PAGE_DEFAUT;

		if (pPageSize < TAILLE_MIN) {
			this.pageSize = TAILLE_DEFAUT;
		} else {
			this.pageSize = pPageSize;
		}

		if (pTris != null) {
			this.tris = new ArrayList<TriSpec>(pTris);
		} else {
			this.tris = new ArrayList<TriSpec>();
		}
	}

	/**
	 * @return pageNumber
	 */
	public int getPageNumber() {
		return this.pageNumber;
	}

	/**
	 * @param pPageNumber : int
	 */
	public void setPageNumber(final int pPageNumber) {
		this.pageNumber = (pPageNumber >= 0) ? pPageNumber : PAGE_DEFAUT;
	}

	/**
	 * @return pageSize
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * @param pPageSize : int
	 */
	public void setPageSize(final int pPageSize) {
		this.pageSize = (pPageSize >= TAILLE_MIN) ? pPageSize : TAILLE_DEFAUT;
	}

	/**
	 * @return tris (copie défensive, non null)
	 */
	public List<TriSpec> getTris() {
		if (this.tris == null) {
			return Collections.emptyList();
		}
		return new ArrayList<TriSpec>(this.tris);
	}

	/**
	 * @param pTris : List&lt;TriSpec&gt;
	 */
	public void setTris(final List<TriSpec> pTris) {
		if (pTris != null) {
			this.tris = new ArrayList<TriSpec>(pTris);
		} else {
			this.tris = new ArrayList<TriSpec>();
		}
	}
}
