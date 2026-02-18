package levy.daniel.application.model.dto.produittype;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduitI;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * Convertisseur d'un Objet métier {@link Produit}
 * vers {@link ProduitDTO.OutputDTO}.
 * </p>
 *
 * <p>Classe utilitaire stateless.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 18 janvier 2026
 */
public final class ConvertisseurMetierToOutputDTOProduit {

	/*-------------------------------------------------------------------*/

	/**
	 * <div>
	 * <p>Constructeur d'arité nulle par défaut.</p>
	 * <p>Constructeur privé pour bloquer l'instanciation (utilitaire).</p>
	 * </div>
	 */
	private ConvertisseurMetierToOutputDTOProduit() {
		super();
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Convertit un {@link Produit}
	 * métier en
	 * {@link ProduitDTO.OutputDTO}.</p>
	 * </div>
	 *
	 * @param pProduit : Produit métier
	 * @return ProduitDTO.OutputDTO ou {@code null} si pProduit est null
	 */
	public static ProduitDTO.OutputDTO convert(
			final Produit pProduit) {

		if (pProduit == null) {
			return null;
		}

		final String typeProduit = extractTypeProduit(
				pProduit);

		final String sousTypeProduit = extractSousTypeProduit(
				pProduit);

		return new ProduitDTO.OutputDTO(
				pProduit.getIdProduit(),
				typeProduit,
				sousTypeProduit,
				pProduit.getProduit()
		);
	}


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit un List&lt;{@link Produit}&gt; 
	 * métier en
	 * List&lt;{@link ProduitDTO.OutputDTO}&gt;.
	 * </p>
	 * <ul>
	 * <li>return null si pList == null.</li>
	 * <li>utilise un LinkedHashSet pour garantir l'unicité.</li>
	 * <li>convertit chaque objet métier en DTO.</li>
	 * <li>retourne une Liste convertie.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pList : List&lt;Produit&gt; : 
	 * Liste à convertir.
	 * @return List&lt;ProduitDTO.OutputDTO&gt; : 
	 * Liste convertie.
	 */
	public static List<ProduitDTO.OutputDTO> convertList(
			final List<Produit> pList) {
		
		/* return null si pList == null. */
		if (pList == null) {
			return null;
		}
		
		/* utilise un LinkedHashSet pour garantir l'unicité. */
		final Set<ProduitDTO.OutputDTO> uniques 
			= new LinkedHashSet<ProduitDTO.OutputDTO>();
		
		for (final Produit tp : pList) {
			
			if (tp == null) {
				continue;
			}
			
			/* convertit chaque objet métier en DTO. */
			final ProduitDTO.OutputDTO dto = convert(tp);
			
			uniques.add(dto);			
		}
		
		/* retourne une Liste convertie. */
		return new ArrayList<ProduitDTO.OutputDTO>(uniques);
	}

	
	
	/**
	 * <div>
	 * <p>Extrait le libellé du TypeProduit parent (grand-parent).</p>
	 *
	 * <p>Lazy-safe : n'accède qu'aux getters publics.</p>
	 * </div>
	 *
	 * @param pProduit : Produit
	 * @return String (peut être {@code null})
	 */
	private static String extractTypeProduit(
			final ProduitI pProduit) {

		if (pProduit == null) {
			return null;
		}

		final SousTypeProduitI stp = pProduit.getSousTypeProduit();

		if (stp == null) {
			return null;
		}

		final TypeProduitI tp = stp.getTypeProduit();

		if (tp == null) {
			return null;
		}

		return tp.getTypeProduit();
	}

	
	
	/**
	 * <div>
	 * <p>Extrait le libellé du SousTypeProduit parent.</p>
	 *
	 * <p>Lazy-safe : n'accède qu'aux getters publics.</p>
	 * </div>
	 *
	 * @param pProduit : Produit
	 * @return String (peut être {@code null})
	 */
	private static String extractSousTypeProduit(
			final ProduitI pProduit) {

		if (pProduit == null) {
			return null;
		}

		final SousTypeProduitI stp = pProduit.getSousTypeProduit();

		if (stp == null) {
			return null;
		}

		return stp.getSousTypeProduit();
	}
}
