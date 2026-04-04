package levy.daniel.application.model.dto.produittype;

import java.util.ArrayList;
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
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p style="font-weight:bold;">Convertit un {@link TypeProduit}
	 * métier en
	 * {@link TypeProduitDTO.OutputDTO}.</p>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduit métier
	 * @return TypeProduitDTO.OutputDTO ou {@code null} 
	 * si pTypeProduit est null
	 */
	public static TypeProduitDTO.OutputDTO convert(
			final TypeProduit pTypeProduit) {

		if (pTypeProduit == null) {
			return null;
		}

		/*
		 * Extrait toujours une liste non nulle et mutable
		 * pour conserver une sémantique homogène côté DTO.
		 */
		final List<String> sousTypeProduits = extractSousTypeProduits(
				pTypeProduit);

		return new TypeProduitDTO.OutputDTO(
				pTypeProduit.getIdTypeProduit(),
				pTypeProduit.getTypeProduit(),
				sousTypeProduits);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit une List&lt;{@link TypeProduit}&gt;
	 * métier en
	 * List&lt;{@link TypeProduitDTO.OutputDTO}&gt;.
	 * </p>
	 * <ul>
	 * <li>retourne null si pList == null ;</li>
	 * <li>utilise un LinkedHashSet pour garantir l'unicité ;</li>
	 * <li>convertit chaque objet métier en DTO ;</li>
	 * <li>ignore les éléments null ;</li>
	 * <li>retourne une liste convertie en conservant l'ordre
	 * de première apparition.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pList : List&lt;TypeProduit&gt; :
	 * liste à convertir
	 * @return List&lt;TypeProduitDTO.OutputDTO&gt; :
	 * liste convertie
	 */
	public static List<TypeProduitDTO.OutputDTO> convertList(
			final List<TypeProduit> pList) {

		/* Retourne null si la liste source est absente. */
		if (pList == null) {
			return null;
		}

		/*
		 * Utilise un LinkedHashSet pour garantir l'unicité
		 * tout en conservant l'ordre de première apparition.
		 */
		final Set<TypeProduitDTO.OutputDTO> uniques =
				new LinkedHashSet<TypeProduitDTO.OutputDTO>();

		for (final TypeProduit tp : pList) {

			/*
			 * Ignore explicitement les éléments null
			 * pour rendre la conversion robuste.
			 */
			if (tp == null) {
				continue;
			}

			/* Convertit chaque objet métier en DTO. */
			final TypeProduitDTO.OutputDTO dto = convert(tp);

			uniques.add(dto);
		}

		/* Retourne une nouvelle liste à partir du set ordonné. */
		return new ArrayList<TypeProduitDTO.OutputDTO>(uniques);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Extrait la liste des libellés des SousTypeProduits enfants.</p>
	 *
	 * <p>Lazy-safe : n'accède qu'aux getters publics.</p>
	 *
	 * <p>
	 * La méthode retourne toujours une liste :
	 * </p>
	 * <ul>
	 * <li>non nulle ;</li>
	 * <li>mutable ;</li>
	 * <li>homogène, même quand la source est null
	 * ou ne contient aucun enfant exploitable.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduit
	 * @return List&lt;String&gt; : jamais null, toujours mutable
	 */
	private static List<String> extractSousTypeProduits(
			final TypeProduitI pTypeProduit) {

		/*
		 * Retourne toujours une ArrayList mutable
		 * pour garantir une sémantique homogène
		 * côté DTO, y compris dans les cas vides.
		 */
		final List<String> result = new ArrayList<String>();

		if (pTypeProduit == null) {
			return result;
		}

		if (pTypeProduit.getSousTypeProduits() == null) {
			return result;
		}

		for (final SousTypeProduitI stp : pTypeProduit.getSousTypeProduits()) {

			/*
			 * Ignore les enfants null
			 * et les libellés null.
			 */
			if (stp == null) {
				continue;
			}

			if (stp.getSousTypeProduit() == null) {
				continue;
			}

			result.add(stp.getSousTypeProduit());
		}

		return result;
		
	} // __________________________________________________________________
	
	

} // FIN DE LA CLASSE ConvertisseurMetierToOutputDTOTypeProduit.___________