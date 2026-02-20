/* ********************************************************************* */
/* ********************** REPOSITORY DAO JPA *************************** */
/* ********************************************************************* */
package levy.daniel.application.persistence.metier.produittype.dao.daosJPA;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">INTERFACE ProduitDaoJPA.java :</p>
 *
 * <p>
 * Cette interface modélise :
 * un <span style="font-weight:bold;">Repository JPA</span>
 * pour l'Entity JPA <span style="font-weight:bold;">ProduitJPA</span>.
 * </p>
 *
 * <p>
 * Elle est utilisée par l'implémentation gateway
 * {@code ProduitGatewayJPAService} pour :
 * </p>
 * <ul>
 * <li>créer / modifier une Entity via {@link #save(ProduitJPA)}</li>
 * <li>lister toutes les Entities via {@link #findAll()}</li>
 * <li>lister toutes les Entities par pages via 
 * {@link #findAll(Pageable)}</li>
 * <li>rechercher par libellé exact (insensible à la casse) 
 * via {@link #findByProduitIgnoreCase(String)}</li>
 * <li>rechercher par libellé contenant (insensible à la casse) 
 * via {@link #findByProduitContainingIgnoreCase(String)}</li>
 * <li>rechercher par parent via 
 * {@link #findAllBySousTypeProduit(SousTypeProduitJPA)}</li>
 * <li>rechercher par ID via {@link #findById(Long)}</li>
 * <li>compter via {@link #count()}</li>
 * <li>supprimer via {@link #delete(ProduitJPA)}</li>
 * </ul>
 * </div>
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 28 déc. 2025
 */
@Repository("ProduitDaoJPA")
public interface ProduitDaoJPA extends 
						JpaRepository<ProduitJPA, Long> {



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche en base une collection de {@link ProduitJPA} 
	 * en fonction du libellé exact (insensible à la casse)
	 * contenu dans le champ <code>pLibelle</code>.
	 * </p>
	 * <ul>
	 * <li>Retourne une <span style="font-weight:bold;">
	 * liste vide</span> (pas {@code null}) 
	 * si la recherche ne trouve pas.</li>
	 * <li>ATTENTION : le Modèle Conceptuel de Données MCD
	 * et les Entities indiquent que
	 * le libellé n'est
	 * <span style="font-weight:bold;">pas forcément unique</span>
	 * pour un {@link Produit}.</li>
	 * <li>C'est le couple [parent, libelle]
	 * qui doit être unique (Cf equals/HashCode
	 * d'un {@link Produit}).</li>
	 * <li>Le parent d'un {@link Produit} 
	 * est un {@link SousTypeProduit}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String : 
	 * libellé exact (insensible à la casse) 
	 * comme "chemise manche longue".
	 * @return List&lt;ProduitJPA&gt; : 
	 * la collection d'Entities trouvée, ou liste vide si absente.
	 */
	List<ProduitJPA> findByProduitIgnoreCase(String pLibelle);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne la List&lt;ProduitJPA&gt; 
	 * des {@link ProduitJPA}
	 * dont le libellé <code>produit</code> contient pContenu 
	 * (insensible à la casse).
	 * </p>
	 * <ul>
	 * <li>Retourne une liste vide si la recherche ne trouve rien
	 * (comportement Spring Data).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu : String : 
	 * contenu partiel (ex : "chemise manc") insensible à la casse.
	 * @return List&lt;ProduitJPA&gt; : 
	 * liste des résultats (éventuellement vide),
	 * jamais {@code null}.
	 */
	List<ProduitJPA> findByProduitContainingIgnoreCase(
			String pContenu);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne toutes les {@link ProduitJPA} du stockage.
	 * </p>
	 * </div>
	 *
	 * @return List&lt;ProduitJPA&gt; : 
	 * liste complète (éventuellement vide).
	 */
	@Override
	List<ProduitJPA> findAll();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne toutes les {@link ProduitJPA} du stockage par pages.
	 * </p>
	 * <ul>
	 * <li>Ne retourne jamais {@code null} en pratique avec Spring Data,
	 * mais le gateway protège tout de même ce cas.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pPageable : Pageable :
	 * objet de pagination.
	 * @return Page&lt;ProduitJPA&gt; : 
	 * liste complète (éventuellement vide) paginée.
	 */
	@Override
	Page<ProduitJPA> findAll(Pageable pPageable);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne la liste des {@link ProduitJPA} 
	 * rattachés au parent {@link SousTypeProduitJPA}.
	 * </p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitJPA : 
	 * parent (entity).
	 * @return List&lt;ProduitJPA&gt; : 
	 * résultats (éventuellement vides), jamais {@code null}.
	 */
	List<ProduitJPA> findAllBySousTypeProduit(
			SousTypeProduitJPA pSousTypeProduit);



}
