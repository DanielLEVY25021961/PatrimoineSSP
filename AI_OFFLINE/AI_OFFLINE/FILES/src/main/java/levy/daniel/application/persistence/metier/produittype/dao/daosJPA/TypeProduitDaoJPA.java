/* ********************************************************************* */
/* ********************** REPOSITORY DAO JPA *************************** */
/* ********************************************************************* */
package levy.daniel.application.persistence.metier.produittype.dao.daosJPA;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">INTERFACE TypeProduitDaoJPA.java :</p>
 *
 * <p>
 * Cette interface modélise :
 * un <span style="font-weight:bold;">Repository JPA</span>
 * pour l'Entity JPA <span style="font-weight:bold;">
 * TypeProduitJPA</span>.
 * </p>
 *
 * <p>
 * Elle est utilisée par l'implémentation gateway
 * {@code TypeProduitGatewayJPAService} pour :
 * </p>
 * <ul>
 * <li>créer / modifier une Entity via {@link #save(TypeProduitJPA)}</li>
 * <li>lister toutes les Entities via {@link #findAll()}</li>
 * <li>lister toutes les Entities par pages via {@link #findAll(Pageable)}</li>
 * <li>rechercher par libellé exact (insensible à la casse) 
 * via {@link #findByTypeProduitIgnoreCase(String)}</li>
 * <li>rechercher par libellé contenant (insensible à la casse) 
 * via {@link #findByTypeProduitContainingIgnoreCase(String)}</li>
 * <li>rechercher par ID via {@link #findById(Long)}</li>
 * <li>compter via {@link #count()}</li>
 * <li>supprimer via {@link #delete(TypeProduitJPA)}</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 28 décembre 2025
 */
@Repository("TypeProduitDaoJPA")
public interface TypeProduitDaoJPA
        extends JpaRepository<TypeProduitJPA, Long> {



    /**
     * <div>
     * <p style="font-weight:bold;">
     * Recherche en base un {@link TypeProduitJPA} 
     * en fonction du libellé exact (insensible à la casse)
     * contenu dans le paramètre <code>pLibelle</code>.
     * </p>
     * <ul>
     * <li>Retourne {@code null} si la recherche ne trouve pas.</li>
     * <li>ATTENTION : le Modèle Conceptuel de Données MCD
     * et les Entities indiquent que
     * le libellé est
     * <span style="font-weight:bold;">forcément unique</span>
     * pour un {@link TypeProduit}.</li>
     * <li>C'est un "Parent" et son libellé doit 
     * forcément être unique dans le stockage (Cf equals/HashCode
     * d'un {@link TypeProduit}).</li>
     * <li>Cette méthode est utilisée par
     * {@code TypeProduitGatewayJPAService.findByTypeProduit(String)}.</li>
     * </ul>
     * </div>
     *
     * @param pLibelle : String :
     * libellé exact (insensible à la casse) comme "vêtement".
     * @return TypeProduitJPA :
     * l'Entity trouvée, ou {@code null} si absente.
     */
    TypeProduitJPA findByTypeProduitIgnoreCase(
            String pLibelle);



    /**
     * <div>
     * <p style="font-weight:bold;">
     * Retourne la List&lt;TypeProduitJPA&gt; des {@link TypeProduitJPA}
     * dont le libellé <code>typeProduit</code> contient pContenu 
     * (insensible à la casse).
     * </p>
     * <ul>
     * <li>Retourne une liste vide si la recherche ne trouve rien
     * (comportement Spring Data).</li>
     * <li>Cette méthode est utilisée par
     * {@code TypeProduitGatewayJPAService.findByTypeProduitRapide(String)}
     * lorsque la recherche n'est pas blank.</li>
     * </ul>
     * </div>
     *
     * @param pContenu : String :
     * contenu partiel (exemple : "vêt") insensible à la casse.
     * @return List&lt;TypeProduitJPA&gt; :
     * liste des résultats (éventuellement vide),
     * jamais {@code null}.
     */
    List<TypeProduitJPA> findByTypeProduitContainingIgnoreCase(
            String pContenu);



    /**
     * <div>
     * <p style="font-weight:bold;">
     * Retourne toutes les {@link TypeProduitJPA} du stockage.
     * </p>
     * <ul>
     * <li>Ne retourne jamais {@code null} en pratique avec Spring Data,
     * mais le gateway baseline v2 protège tout de même ce cas.</li>
     * </ul>
     * </div>
     *
     * @return List&lt;TypeProduitJPA&gt; :
     * liste complète (éventuellement vide).
     */
    @Override
    List<TypeProduitJPA> findAll();



    /**
     * <div>
     * <p style="font-weight:bold;">
     * Retourne toutes les {@link TypeProduitJPA} du stockage par pages.
     * </p>
     * <ul>
     * <li>Ne retourne jamais {@code null} en pratique avec Spring Data,
     * mais le gateway protège tout de même ce cas.</li>
     * <li>Cette méthode est utilisée par
     * {@code TypeProduitGatewayJPAService.rechercherTous()} et
     * {@code TypeProduitGatewayJPAService.findByTypeProduitRapide(String)}
     * lorsque la recherche est blank.</li>
     * </ul>
     * </div>
     *
     * @param pPageable : Pageable :
     * objet de pagination.
     * @return Page&lt;TypeProduitJPA&gt; :
     * liste complète (éventuellement vide) paginée.
     */
    @Override
    Page<TypeProduitJPA> findAll(Pageable pPageable);



}
