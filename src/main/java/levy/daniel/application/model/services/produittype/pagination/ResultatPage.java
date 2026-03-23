/* ********************************************************************* */
/* *************************** PAGINATION ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;


/**
 * <div>
 * <p style="font-weight:bold;">CLASSE ResultatPage.java :</p>
 * <p>Résultat paginé générique et neutre (indépendant de Spring).</p>
 * <p>Encapsule les résultats d'une requête paginée, 
 * avec des métadonnées pour la navigation.</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemples d'utilisation :</p>
 * <ul>
 * <li>Création depuis un repository Spring Data :
 * <pre>
 * Page&lt;ProduitJPA&gt; springPage = produitRepository.findAll(PageRequest.of(0, 10));
 * ResultatPage&lt;ProduitJPA&gt; resultat = ResultatPage.fromSpringPage(springPage);
 * </pre>
 * </li>
 * <li>Vérification de la pagination :
 * <pre>
 * if (resultat.isHasNext()) { ... }
 * </pre>
 * </li>
 * </ul>
 * </div>
 *
 * @param <T> Type des éléments de la page
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 19 janvier 2026
 */
public class ResultatPage<T> implements Serializable {

	// *************************** CONSTANTES ******************************/

	/** Serial UID. */
	private static final long serialVersionUID = 1L;


	// *************************** ATTRIBUTS *******************************/

	/** Contenu de la page. */
	private final List<T> content;

	/** Numéro de page (0-based). */
	private final int pageNumber;

	/** Taille de page. */
	private final int pageSize;

	/** Total d'éléments disponibles. */
	private final long totalElements;

	/** Total de pages. */
	private final int totalPages;


	// ************************* METHODES **********************************/

	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 4.</p>
	 * </div>
	 *
	 * @param pContent : List&lt;T&gt;
	 * @param pPageNumber : int
	 * @param pPageSize : int
	 * @param pTotalElements : long
	 */
	public ResultatPage(
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

		this.pageNumber = (pPageNumber >= 0) ? pPageNumber : 0;
		this.pageSize = (pPageSize >= 1) ? pPageSize : 1;
		this.totalElements = (pTotalElements >= 0L) ? pTotalElements : 0L;

		final long pages = (this.pageSize > 0)
				? ((this.totalElements + this.pageSize - 1L) / this.pageSize)
				: 0L;

		this.totalPages = (pages > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) pages;
	}

	
	/**
	 * @return content (copie défensive, non null)
	 */
	public List<T> getContent() {
		if (this.content == null) {
			return Collections.emptyList();
		}
		return new ArrayList<T>(this.content);
	}

	/**
	 * @return pageNumber
	 */
	public int getPageNumber() {
		return this.pageNumber;
	}

	/**
	 * @return pageSize
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * @return totalElements
	 */
	public long getTotalElements() {
		return this.totalElements;
	}

	/**
	 * @return totalPages
	 */
	public int getTotalPages() {
		return this.totalPages;
	}

	/**
	 * @return true si une page suivante existe
	 */
	public boolean isHasNext() {
		return this.pageNumber + 1 < this.totalPages;
	}

	/**
	 * @return true si une page précédente existe
	 */
	public boolean isHasPrevious() {
		return this.pageNumber > 0;
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Crée un ResultatPage à partir d'un Page Spring Data.</p>
	 * </div>
	 * 
	 * @param pSpringPage : Page&lt;T&gt; : 
	 * La page Spring Data à convertir.
	 * @return ResultatPage&lt;T&gt; : 
	 * Un ResultatPage équivalent.
	 */
	public static <T> ResultatPage<T> fromSpringPage(
			final Page<T> pSpringPage) {
	    return new ResultatPage<>(
	        pSpringPage.getContent(),
	        pSpringPage.getNumber(),
	        pSpringPage.getSize(),
	        pSpringPage.getTotalElements()
	    );
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * détermine si une Page est vide.</p>
	 * </div>
	 * 
	 * @return boolean : true si la page ne contient aucun élément.
	 */
	public boolean isEmpty() {
	    return this.content.isEmpty();
	}
	
}
