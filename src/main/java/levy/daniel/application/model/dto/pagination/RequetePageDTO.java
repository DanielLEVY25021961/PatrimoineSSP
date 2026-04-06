/* ********************************************************************* */
/* ****************** RequetePageDTO DTO ******************************* */
/* ********************************************************************* */
package levy.daniel.application.model.dto.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE RequetePageDTO.java :
 * </p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette CLASSE modélise :
 * <span style="font-weight:bold;">une requête de pagination DTO</span>,
 * c'est à dire l'objet de transport utilisé par les VUES
 * pour demander une page, une taille de page
 * et éventuellement des critères de tri.</li>
 * <li>Cette CLASSE est un objet de transport partagé
 * entre les VUES, les CONTROLLERS et les SERVICES UC,
 * sans logique métier ni dépendance technologique.</li>
 * <li>Convention contractuelle :
 * <span style="font-weight:bold;">pageNumber est humain</span>,
 * commence à <span style="font-weight:bold;">1</span>
 * et n'est donc pas 0-based.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 6 avril 2026
 */
public final class RequetePageDTO implements Serializable {

	/* *************************** CONSTANTES *************************** */

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <div>
	 * <p>Numéro de page "humain" (pas 0-based) par défaut 
	 * (appliqué par le constructeur d'arité nulle).</p>
	 * </div>
	 */
	public static final int PAGE_DEFAUT = 1;

	/**
	 * <div>
	 * <p>Taille de page par défaut 
	 * (appliquée par le constructeur d'arité nulle).</p>
	 * </div>
	 */
	public static final int TAILLE_DEFAUT = 10;

	/**
	 * <div>
	 * <p>Taille minimale autorisée 
	 * (nombre minimal d'enregistrements par page).</p>
	 * </div>
	 */
	public static final int TAILLE_MIN = 1;

	/* *************************** ATTRIBUTS **************************** */

	/**
	 * <div>
	 * <p>Numéro de page "humain" (pas 0-based) demandé par la VUE.</p>
	 * </div>
	 */
	private int pageNumber;

	/**
	 * <div>
	 * <p>Taille de page demandée par la VUE
	 * (nombre d'enregistrements dans la page).</p>
	 * </div>
	 */
	private int pageSize;

	/**
	 * <div>
	 * <p>Liste des critères de tri demandés par la VUE.</p>
	 * </div>
	 */
	private List<TriSpecDTO> tris;

	/* *************************** METHODES ***************************** */

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public RequetePageDTO() {
		this(PAGE_DEFAUT, TAILLE_DEFAUT, null);
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pPageNumber : int :
	 * le numéro de page humain demandé.
	 * @param pPageSize : int :
	 * la taille de page demandée.
	 */
	public RequetePageDTO(
			final int pPageNumber,
			final int pPageSize) {
		this(pPageNumber, pPageSize, null);
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE 3.</p>
	 * </div>
	 *
	 * @param pPageNumber : int :
	 * le numéro de page humain demandé.
	 * @param pPageSize : int :
	 * la taille de page demandée.
	 * @param pTris : List<TriSpecDTO> :
	 * la liste des critères de tri demandés.
	 */
	public RequetePageDTO(
			final int pPageNumber,
			final int pPageSize,
			final List<TriSpecDTO> pTris) {
		super();

		this.pageNumber
			= (pPageNumber >= PAGE_DEFAUT)
				? pPageNumber
				: PAGE_DEFAUT;

		this.pageSize
			= (pPageSize >= TAILLE_MIN)
				? pPageSize
				: TAILLE_DEFAUT;

		if (pTris != null) {
			this.tris = new ArrayList<TriSpecDTO>(pTris);
		} else {
			this.tris = new ArrayList<TriSpecDTO>();
		}
	}

	
	
	/**
	 * <div>
	 * <p>Getter du numéro de page "humain" demandé.</p>
	 * </div>
	 *
	 * @return int : <code>this.pageNumber</code> :
	 * le numéro de page "humain" demandé.
	 */
	public int getPageNumber() {
		return this.pageNumber;
	}

	
	
	/**
	 * <div>
	 * <p>Setter du numéro de page "humain" demandé.</p>
	 * </div>
	 *
	 * @param pPageNumber : int :
	 * valeur à passer à <code>this.pageNumber</code> :
	 * le numéro de page "humain" demandé.
	 */
	public void setPageNumber(final int pPageNumber) {
		this.pageNumber
			= (pPageNumber >= PAGE_DEFAUT)
				? pPageNumber
				: PAGE_DEFAUT;
	}

	
	
	/**
	 * <div>
	 * <p>Getter de la taille de page demandée.</p>
	 * </div>
	 *
	 * @return int : <code>this.pageSize</code> :
	 * la taille de page demandée
	 * (nombre d'enregistrements dans la page).
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	
	
	/**
	 * <div>
	 * <p>Setter de la taille de page demandée.</p>
	 * </div>
	 *
	 * @param pPageSize : int :
	 * valeur à passer à <code>this.pageSize</code> :
	 * la taille de page demandée
	 * (nombre d'enregistrements dans la page).
	 */
	public void setPageSize(final int pPageSize) {
		this.pageSize
			= (pPageSize >= TAILLE_MIN)
				? pPageSize
				: TAILLE_DEFAUT;
	}

	
	
	/**
	 * <div>
	 * <p>Getter de la liste des critères de tri demandés.</p>
	 * </div>
	 *
	 * @return List&lt;TriSpecDTO&gt; :
	 * la liste des critères de tri demandés,
	 * sous forme de copie défensive non nulle.
	 */
	public List<TriSpecDTO> getTris() {

		if (this.tris == null) {
			return Collections.emptyList();
		}

		return new ArrayList<TriSpecDTO>(this.tris);
	}

	
	
	/**
	 * <div>
	 * <p>Setter de la liste des critères de tri demandés.</p>
	 * </div>
	 *
	 * @param pTris : List<TriSpecDTO> :
	 * la liste des critères de tri demandés.
	 */
	public void setTris(final List<TriSpecDTO> pTris) {

		if (pTris != null) {
			this.tris = new ArrayList<TriSpecDTO>(pTris);
		} else {
			this.tris = new ArrayList<TriSpecDTO>();
		}
	}

	
	
}