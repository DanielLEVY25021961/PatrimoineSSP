package levy.daniel.application.model.dto.produittype;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduitI;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * Convertisseur d'un Objet métier {@link TypeProduit}
 * vers {@link TypeProduitDTO.OutputDTO}.
 * </p>
 * 
 * <p>Classe utilitaire stateless.</p>
 * 
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 janvier 2026
 */
public final class ConvertisseurMetierToOutputDTOTypeProduit {

	/*-------------------------------------------------------------------*/
	
	/**
	 * <div>
	 * <p>Constructeur d'arité nulle par défaut.</p>
	 * <p>Constructeur privé pour bloquer l'instanciation (utilitaire).</p>
	 * </div>
	 */
	private ConvertisseurMetierToOutputDTOTypeProduit() {
		super();
	}


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Convertit un {@link TypeProduit} 
	 * métier en
	 * {@link TypeProduitDTO.OutputDTO}.</p>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduit métier
	 * @return TypeProduitDTO.OutputDTO ou {@code null} si pTypeProduit est null
	 */
	public static TypeProduitDTO.OutputDTO convert(
			final TypeProduit pTypeProduit) {

		if (pTypeProduit == null) {
			return null;
		}

		final List<String> sousTypeProduits = extractSousTypeProduits(
				pTypeProduit);

		return new TypeProduitDTO.OutputDTO(
				pTypeProduit.getIdTypeProduit(),
				pTypeProduit.getTypeProduit(),
				sousTypeProduits
		);
	}


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit un List&lt;{@link TypeProduit}&gt; 
	 * métier en
	 * List&lt;{@link TypeProduitDTO.OutputDTO}&gt;.
	 * </p>
	 * <ul>
	 * <li>return null si pList == null.</li>
	 * <li>utilise un LinkedHashSet pour garantir l'unicité.</li>
	 * <li>convertit chaque objet métier en DTO.</li>
	 * <li>retourne une Liste convertie.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pList : List&lt;TypeProduit&gt; : 
	 * Liste à convertir.
	 * @return List&lt;TypeProduitDTO.OutputDTO&gt; : 
	 * Liste convertie.
	 */
	public static List<TypeProduitDTO.OutputDTO> convertList(
			final List<TypeProduit> pList) {
		
		/* return null si pList == null. */
		if (pList == null) {
			return null;
		}
		
		/* utilise un LinkedHashSet pour garantir l'unicité. */
		final Set<TypeProduitDTO.OutputDTO> uniques 
			= new LinkedHashSet<TypeProduitDTO.OutputDTO>();
		
		for (final TypeProduit tp : pList) {
			
			if (tp == null) {
				continue;
			}
			
			/* convertit chaque objet métier en DTO. */
			final TypeProduitDTO.OutputDTO dto = convert(tp);
			
			uniques.add(dto);			
		}
		
		/* retourne une Liste convertie. */
		return new ArrayList<TypeProduitDTO.OutputDTO>(uniques);
	}
	
	
	
	/**
	 * <div>
	 * <p>Extrait la liste des libellés des SousTypeProduits enfants.</p>
	 *
	 * <p>Lazy-safe : n'accède qu'aux getters publics.</p>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduit
	 * @return List&lt;String&gt; : jamais null
	 */
	private static List<String> extractSousTypeProduits(
	        final TypeProduitI pTypeProduit) {

	    if (pTypeProduit == null) {
	        return Collections.emptyList();
	    }

	    if (pTypeProduit.getSousTypeProduits() == null) {
	        return Collections.emptyList();
	    }

	    final List<String> result = new ArrayList<>();

	    for (final SousTypeProduitI stp : pTypeProduit.getSousTypeProduits()) {
	        if (stp != null && stp.getSousTypeProduit() != null) {
	            result.add(stp.getSousTypeProduit());
	        }
	    }

	    return result;
	}
}
