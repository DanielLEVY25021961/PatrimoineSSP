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
 * c'est à dire l'ensemble des enregistrements de Type T dans une page 
 * correspondant à une RequetePageDTO.
 * </li>
 * <li>Attention : classe utilisant la généricité 
 * utilisable pour tout Type T.</li>
 * <li>Cette CLASSE est un objet de transport
 * partagé entre les VUES, les CONTROLLERS
 * et les SERVICES UC (elle ne porte pas de logique métier 
 * et ne dépend d’aucune technologie).</li>
 * <li>Elle encapsule le contenu paginé
 * et les métadonnées utiles à la navigation.</li>
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

	/* *************************** ATTRIBUTS **************************** */

	/**
	 * Contenu de la page (liste des enregistrements dans la page).
	 */
	private final List<T> content;

	/**
	 * Numéro de page (0-based).
	 */
	private final int pageNumber;

	/**
	 * Taille de la page.
	 */
	private final int pageSize;

	/**
	 * Nombre total d'éléments de Type T dans le stockage.
	 */
	private final long totalElements;

	/**
	 * Nombre total de pages 
	 * (prenant en compte la taille de la page indiquée 
	 * dans un RequetePageDTO).
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
	 * le numéro de page.
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
			this.content = new ArrayList<>(pContent);
		} else {
			this.content = new ArrayList<>();
		}

		this.pageNumber = (pPageNumber >= 0) ? pPageNumber : 0;
		this.pageSize = (pPageSize >= 1) ? pPageSize : 1;
		this.totalElements = (pTotalElements >= 0L) ? pTotalElements : 0L;

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
	 * <p>Getter du Contenu de la page 
	 * (liste des enregistrements dans la page).</p>
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

		return new ArrayList<>(this.content);
	}

	
	
	/**
	 * <div>
	 * <p>Getter du Numéro de page (0-based).</p>
	 * </div>
	 * 
	 * @return int : <code>this.pageNumber</code> :
	 * le numéro de page.
	 */
	public int getPageNumber() {
		return this.pageNumber;
	}

	
	
	/**
	 * <div>
	 * <p>Getter de la Taille de la page.</p>
	 * </div>
	 * 
	 * @return int : <code>this.pageSize</code> : 
	 * la taille de page.
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	
	
	/**
	 * <div>
	 * <p>Getter du Nombre total d'éléments de Type T dans le stockage.</p>
	 * </div>
	 * 
	 * @return long : <code>this.totalElements</code> : 
	 * le nombre total d'éléments.
	 */
	public long getTotalElements() {
		return this.totalElements;
	}

	
	
	/**
	 * <div>
	 * <p>Getter du Nombre total de pages 
	 * (prenant en compte la taille de la page indiquée 
	 * dans un RequetePageDTO).</p>
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
	 * <p>boolean indiquant si une page a une page suivante.</p>
	 * </div>
	 * 
	 * @return boolean :
	 * true si une page suivante existe.
	 */
	public boolean isHasNext() {
		return this.pageNumber + 1 < this.totalPages;
	}

	
	
	/**
	 * <div>
	 * <p>boolean indiquant si une page a une page précédente.</p>
	 * </div>
	 * 
	 * @return boolean :
	 * true si une page précédente existe.
	 */
	public boolean isHasPrevious() {
		return this.pageNumber > 0;
	}
	
	

}