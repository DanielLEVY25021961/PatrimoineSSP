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
 * </ul>
 * <li>Cette CLASSE modélise :
 * <span style="font-weight:bold;">une requête de pagination DTO</span>, 
 * c'est à dire l'objet de transport qui permet à l'utilisateur d'indiquer 
 * à l'pplication quel numéro (0-based) de page il veut, 
 * combien d'enregistrements par page, ...
 * </li>
 * <li>Cette CLASSE est un objet de transport
 * partagé entre les VUES, les CONTROLLERS
 * et les SERVICES UC.</li>
 * <li>Cette CLASSE est purement contractuelle
 * et ne dépend d'aucune technologie.</li>
 *
 * <p>Convention contractuelle :
 * <span style="font-weight:bold;">pageNumber est 0-based</span>.</p>
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
	 * Numéro de page par défaut (base 0).
	 */
	public static final int PAGE_DEFAUT = 0;

	/**
	 * Taille de page par défaut.
	 */
	public static final int TAILLE_DEFAUT = 20;

	/**
	 * Taille minimale.
	 */
	public static final int TAILLE_MIN = 1;

	/* *************************** ATTRIBUTS **************************** */

	/**
	 * Numéro de page (base 0).
	 */
	private int pageNumber;

	/**
	 * Taille de page (nombre d'enregistrements par page).
	 */
	private int pageSize;

	/**
	 * Liste des tris.
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
	 * le numéro de page demandé.
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
	 * le numéro de page demandé.
	 * @param pPageSize : int :
	 * la taille de page demandée.
	 * @param pTris : List<TriSpecDTO> :
	 * la liste des tris demandés.
	 */
	public RequetePageDTO(
			final int pPageNumber,
			final int pPageSize,
			final List<TriSpecDTO> pTris) {
		super();

		this.pageNumber
			= (pPageNumber >= 0)
				? pPageNumber
				: PAGE_DEFAUT;

		if (pPageSize < TAILLE_MIN) {
			this.pageSize = TAILLE_DEFAUT;
		} else {
			this.pageSize = pPageSize;
		}

		if (pTris != null) {
			this.tris = new ArrayList<>(pTris);
		} else {
			this.tris = new ArrayList<>();
		}
	}

	
	
	/**
	 * <div>
	 * <p>Getter du numéro de page (0-based).</p>
	 * </div>
	 * 
	 * @return int : <code>this.pageNumber</code> : 
	 * le numéro de page demandé (0-based).
	 */
	public int getPageNumber() {
		return this.pageNumber;
	}

	
	
	/**
	 * <div>
	 * <p>Setter du numéro de page (0-based).</p>
	 * </div>
	 * 
	 * @param pPageNumber : int : valeur à passer à 
	 * <code>this.pageNumber</code> : 
	 * le numéro de page demandé (0-based).
	 */
	public void setPageNumber(final int pPageNumber) {
		this.pageNumber
			= (pPageNumber >= 0)
				? pPageNumber
				: PAGE_DEFAUT;
	}

	
	
	/**
	 * <div>
	 * <p>Getter de la taille de la page demandée 
	 * (nombre d'enregistrements dans la page).</p>
	 * </div>
	 * 
	 * @return int : <code>this.pageSize</code> : 
	 * la taille de page demandée.
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	
	
	/**
	 * <div>
	 * <p>Setter de la taille de la page demandée 
	 * (nombre d'enregistrements dans la page).</p>
	 * </div>
	 * 
	 * @param pPageSize : int : valeur à passer à 
	 * <code>this.pageSize</code> : 
	 * la taille de page demandée.
	 */
	public void setPageSize(final int pPageSize) {
		this.pageSize
			= (pPageSize >= TAILLE_MIN)
				? pPageSize
				: TAILLE_DEFAUT;
	}

	
	
	/**
	 * <div>
	 * <p>Getter de la liste des tris disponibles.</p>
	 * </div>
	 * 
	 * @return List&lt;TriSpecDTO&gt; :
	 * la liste des tris,
	 * sous forme de copie défensive non nulle (new ArrayList<>(this.tris)).
	 */
	public List<TriSpecDTO> getTris() {

		if (this.tris == null) {
			return Collections.emptyList();
		}

		return new ArrayList<TriSpecDTO>(this.tris);
	}

	
	
	/**
	 * <div>
	 * <p>Setter de la liste des tris disponibles.</p>
	 * </div>
	 * 
	 * @param pTris : List<TriSpecDTO> :
	 * la liste des tris.
	 */
	public void setTris(final List<TriSpecDTO> pTris) {

		if (pTris != null) {
			this.tris = new ArrayList<TriSpecDTO>(pTris);
		} else {
			this.tris = new ArrayList<TriSpecDTO>();
		}
	}

	
	
}