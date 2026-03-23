package levy.daniel.application.model.dto.produittype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduitI;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * Convertisseur d'un Objet métier {@link SousTypeProduit}
 * vers {@link SousTypeProduitDTO.OutputDTO}.
 * </p>
 *
 * <p>Classe utilitaire stateless.</p>
 * 
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 18 janvier 2026
 */
public final class ConvertisseurMetierToOutputDTOSousTypeProduit {

	/*-------------------------------------------------------------------*/

	/**
	 * <div>
	 * <p>Constructeur d'arité nulle par défaut.</p>
	 * <p>Constructeur privé pour bloquer l'instanciation (utilitaire).</p>
	 * </div>
	 */
	private ConvertisseurMetierToOutputDTOSousTypeProduit() {
		super();
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Convertit un {@link SousTypeProduit} 
	 * métier en
	 * {@link SousTypeProduitDTO.OutputDTO}.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduit métier
	 * @return SousTypeProduitDTO.OutputDTO ou {@code null} si pSousTypeProduit est null
	 */
	public static SousTypeProduitDTO.OutputDTO convert(
			final SousTypeProduit pSousTypeProduit) {

		if (pSousTypeProduit == null) {
			return null;
		}

		final String typeProduit = extractTypeProduit(
				pSousTypeProduit);

		final List<String> produits = extractProduits(
				pSousTypeProduit);

		return new SousTypeProduitDTO.OutputDTO(
				pSousTypeProduit.getIdSousTypeProduit(),
				typeProduit,
				pSousTypeProduit.getSousTypeProduit(),
				produits
		);
	}


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit un List&lt;{@link SousTypeProduit}&gt; 
	 * métier en
	 * List&lt;{@link SousTypeProduitDTO.OutputDTO}&gt;.
	 * </p>
	 * <ul>
	 * <li>return null si pList == null.</li>
	 * <li>utilise un LinkedHashSet pour garantir l'unicité.</li>
	 * <li>convertit chaque objet métier en DTO.</li>
	 * <li>retourne une Liste convertie.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pList : List&lt;SousTypeProduit&gt; : 
	 * Liste à convertir.
	 * @return List&lt;SousTypeProduitDTO.OutputDTO&gt; : 
	 * Liste convertie.
	 */
	public static List<SousTypeProduitDTO.OutputDTO> convertList(
			final List<SousTypeProduit> pList) {
		
		/* return null si pList == null. */
		if (pList == null) {
			return null;
		}
		
		/* utilise un LinkedHashSet pour garantir l'unicité. */
		final Set<SousTypeProduitDTO.OutputDTO> uniques 
			= new LinkedHashSet<SousTypeProduitDTO.OutputDTO>();
		
		for (final SousTypeProduit tp : pList) {
			
			if (tp == null) {
				continue;
			}
			
			/* convertit chaque objet métier en DTO. */
			final SousTypeProduitDTO.OutputDTO dto = convert(tp);
			
			uniques.add(dto);			
		}
		
		/* retourne une Liste convertie. */
		return new ArrayList<SousTypeProduitDTO.OutputDTO>(uniques);
	}

	
	
	/**
	 * <div>
	 * <p>Extrait le libellé du TypeProduit parent.</p>
	 *
	 * <p>Lazy-safe : n'accède qu'aux getters publics.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduit
	 * @return String (peut être {@code null})
	 */
	private static String extractTypeProduit(
			final SousTypeProduitI pSousTypeProduit) {

		if (pSousTypeProduit == null) {
			return null;
		}

		final TypeProduitI tp = pSousTypeProduit.getTypeProduit();

		if (tp == null) {
			return null;
		}

		return tp.getTypeProduit();
	}

	
	
	/**
	 * <div>
	 * <p>Extrait la liste des libellés des Produits petits-enfants.</p>
	 *
	 * <p>Lazy-safe : n'accède qu'aux getters publics.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduit
	 * @return List&lt;String&gt; jamais {@code null}
	 */
	private static List<String> extractProduits(
			final SousTypeProduitI pSousTypeProduit) {

		if (pSousTypeProduit == null) {
			return Collections.emptyList();
		}

		if (pSousTypeProduit.getProduits() == null) {
			return Collections.emptyList();
		}

		final List<String> result = new ArrayList<>();

		for (final ProduitI produit : pSousTypeProduit.getProduits()) {
			if (produit != null && produit.getProduit() != null) {
				result.add(produit.getProduit());
			}
		}

		return result;
	}
}
