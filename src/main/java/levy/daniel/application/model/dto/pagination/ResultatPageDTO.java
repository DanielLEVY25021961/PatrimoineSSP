/* ********************************************************************* */
/* ***************************** DTO *********************************** */
/* ********************************************************************* */
package levy.daniel.application.model.dto.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ResultatPageDTO.java :
 * </p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette CLASSE modélise :
 * <span style="font-weight:bold;">un résultat paginé DTO</span>,
 * c'est à dire l'ensemble des enregistrements d'une page
 * ainsi que les métadonnées utiles à la navigation.</li>
 * <li>Attention : classe utilisant la généricité
 * utilisable pour tout Type T.</li>
 * <li>Cette CLASSE est un objet de transport partagé
 * entre les VUES, les CONTROLLERS et les SERVICES UC,
 * sans logique métier ni dépendance technologique.</li>
 * <li>Convention contractuelle :
 * <span style="font-weight:bold;">pageNumber est "humain"</span>,
 * commence à <span style="font-weight:bold;">1</span>
 * et n'est donc pas 0-based.</li>
 * </ul>
 * </div>
 *
 * @param <T> :
 * le type des éléments de la page.
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 6 avril 2026
 */
public final class ResultatPageDTO<T> implements Serializable {

	/* *************************** CONSTANTES *************************** */

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <div>
	 * <p>Numéro de page humain par défaut.</p>
	 * </div>
	 */
	public static final int PAGE_DEFAUT = 1;

	/**
	 * <div>
	 * <p>Taille de page minimale autorisée.</p>
	 * </div>
	 */
	public static final int TAILLE_MIN = 1;

	/* *************************** ATTRIBUTS **************************** */

	/**
	 * <div>
	 * <p>Contenu de la page
	 * (liste des enregistrements de Type T dans la page).</p>
	 * </div>
	 */
	private final List<T> content;

	/**
	 * <div>
	 * <p>Numéro de page humain (pas 0-based).</p>
	 * </div>
	 */
	private final int pageNumber;

	/**
	 * <div>
	 * <p>Taille de la page (nombre d'enregistrements dans la page).</p>
	 * </div>
	 */
	private final int pageSize;

	/**
	 * <div>
	 * <p>Nombre total d'éléments de Type T disponibles dans le stockage.</p>
	 * </div>
	 */
	private final long totalElements;

	/**
	 * <div>
	 * <p>Nombre total de pages calculé à partir de la taille de page
	 * et du nombre total d'éléments de Type T disponibles 
	 * dans le stockage.</p>
	 * </div>
	 */
	private final int totalPages;

	
	
	/* *************************** METHODES ***************************** */

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE 4.</p>
	 * </div>
	 *
	 * @param pContent : List<T> :
	 * le contenu de la page.
	 * @param pPageNumber : int :
	 * le numéro de page humain.
	 * @param pPageSize : int :
	 * la taille de page.
	 * @param pTotalElements : long :
	 * le nombre total d'éléments disponibles.
	 */
	public ResultatPageDTO(
			final List<T> pContent,
			final int pPageNumber,
			final int pPageSize,
			final long pTotalElements) {
		super();

		if (pContent != null) {
			this.content = new ArrayList<T>(pContent);
		} else {
			this.content = new ArrayList<T>();
		}

		this.pageNumber
			= (pPageNumber >= PAGE_DEFAUT)
				? pPageNumber
				: PAGE_DEFAUT;

		this.pageSize
			= (pPageSize >= TAILLE_MIN)
				? pPageSize
				: TAILLE_MIN;

		this.totalElements
			= (pTotalElements >= 0L)
				? pTotalElements
				: 0L;

		final long pages
			= (this.pageSize > 0)
				? ((this.totalElements + this.pageSize - 1L) / this.pageSize)
				: 0L;

		this.totalPages
			= (pages > Integer.MAX_VALUE)
				? Integer.MAX_VALUE
				: (int) pages;
	}

	
	
	/**
	 * <div>
	 * <p>Getter du contenu de la page.</p>
	 * </div>
	 *
	 * @return List<T> :
	 * le contenu de la page,
	 * sous forme de copie défensive non nulle.
	 */
	public List<T> getContent() {

		if (this.content == null) {
			return Collections.emptyList();
		}

		return new ArrayList<T>(this.content);
	}

	
	
	/**
	 * <div>
	 * <p>Getter du numéro de page "humain" (pas 0-based).</p>
	 * </div>
	 *
	 * @return int : <code>this.pageNumber</code> :
	 * le numéro de page "humain" (pas 0-based).
	 */
	public int getPageNumber() {
		return this.pageNumber;
	}

	
	
	/**
	 * <div>
	 * <p>Getter de la taille de la page.</p>
	 * </div>
	 *
	 * @return int : <code>this.pageSize</code> :
	 * la taille de la page.
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	
	
	/**
	 * <div>
	 * <p>Getter du nombre total d'éléments disponibles.</p>
	 * </div>
	 *
	 * @return long : <code>this.totalElements</code> :
	 * le nombre total d'éléments disponibles.
	 */
	public long getTotalElements() {
		return this.totalElements;
	}

	
	
	/**
	 * <div>
	 * <p>Getter du nombre total de pages.</p>
	 * </div>
	 *
	 * @return int : <code>this.totalPages</code> :
	 * le nombre total de pages.
	 */
	public int getTotalPages() {
		return this.totalPages;
	}

	
	
	/**
	 * <div>
	 * <p>Indique si une page suivante existe.</p>
	 * </div>
	 *
	 * @return boolean :
	 * true si une page suivante existe.
	 */
	public boolean isHasNext() {
		return this.totalPages > 0
				&& this.pageNumber < this.totalPages;
	}

	
	
	/**
	 * <div>
	 * <p>Indique si une page précédente existe.</p>
	 * </div>
	 *
	 * @return boolean :
	 * true si une page précédente existe.
	 */
	public boolean isHasPrevious() {
		return this.pageNumber > 1;
	}

	
	
}