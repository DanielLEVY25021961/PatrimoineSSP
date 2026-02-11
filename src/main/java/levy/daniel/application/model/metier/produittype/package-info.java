/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;padding : 20px}</style>
 * 
 * 
 * <div>
 * 
 * <p style="font-weight:bold;">CLASSE package-info.java :</p>
 * 
 * 
 * <div>
 * <p>
 * Ce package <code>model.metier.produittype</code> contient toutes les classes 
 * <span style="font-weight:bold;">métier décrivant un PRODUIT (Produit) 
 * typé par un SOUS-TYPE de produit (SousTypeProduit) lui-même déclinant un TYPE DE PRODUIT</span> 
 * </p>
 * 
 * <div>
 * <p>Dans ce modèle, un TypeProduit comme "vêtement" se décline en SousTypeProduit comme :</p>
 * <ul>
 * <li>"vêtement pour homme"</li>
 * <li>"vêtement pour femme"</li>
 * <li>"vêtement pour enfant"</li>
 * </ul>
 * </div>
 * <div>
 * <p>un SousTypeProduit comme "vêtement pour homme" qualifie des Produit comme : </p>
 * <ul>
 * <li>"chemise à manches longues pour homme"</li>
 * <li>"chemise à manches courtes pour homme"</li>
 * <li>"tee-shirt pour homme"</li>
 * </ul>
 * </div>
 * </div>
 * 
 * <div>
 * <h1>Exigences sur un PRODUIT typé avec SOUS-TYPE de produit et TYPE de produit</h1>
 * <table style="padding: 10px;border: 1px solid black;border-collapse: collapse">
 * <tr>
 * <th style="padding: 10px;border: 1px solid black;border-collapse: collapse">EXIGENCE</th>
 * <th style="padding: 10px;border: 1px solid black;border-collapse: collapse">DESCRIPTION</th>
 * </tr>
 * <tr>
 * <td style="padding: 10px;border: 1px solid black;border-collapse: collapse">EX_FONCT_PRODUIT_001 : un PRODUIT doit connaitre son SousTypeProduit et son TypeProduit</td>
 * <td style="padding: 10px;border: 1px solid black;border-collapse: collapse">Un PRODUIT comme "tee-shirt homme" doit savoir exposer son SousTypeProduit "vêtement homme" et son TypeProduit "vêtement".</td>
 * </tr>
 * </table>
 * </div>
 * 
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
 * Diagramme de Classes du Produit qualifié par un SousTypeProduit qui est lui-même une déclinaison d'un TypeProduit</p>
 * <p>
 * <img src="../../../../../../../../../javadoc/images/model/metier/produittype/diagramme_de_classes_produit_typé.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 300px />
 * </p>
 * </div>
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
 * TYPE DE PRODUIT TypeProduit qui qualifie un SOUS-TYPE DE PRODUIT SousTypeProduit</p>
 * <table>
 * <tr>
 * <td>
 * <img src="../../../../../../../../../javadoc/images/model/metier/produittype/type_produit.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 200px />
 * </td>
 * <td>
 * <img src="../../../../../../../../../javadoc/images/model/metier/produittype/type_produit-sous_type_produit.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 200px />
 * </td>
 * </tr>
 * </table>
 * </div>
 * 
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
 * Notion de PRODUIT qui précise un Sous-Type de Produit</p>
 * <p>
 * <img src="../../../../../../../../../javadoc/images/model/metier/produittype/produit.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 200px />
 * </p>
 * </div>
 * 
 * <p>
 * On y trouve :
 * <ul>
 * <li>exceptions.</li>
 * <li>xxxx.</li>
 * <li>yyy.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * xxx
 * </p>
 * <p>
 * zzzz
 * </p>
 * <p>
 * rrrr.
 * </p>
 * 
 * </div>
 *
 * <ul>
 * <li>
 * <p>Exemple d'utilisation :</p>
 * <p><code></code></p>
 * </li>
 *  
 * <li>
 * <p>Mots-clé :</p>
 * <p></p>
 * <p></p>
 * <p></p>
 * </li>
 * </ul>
 * 
 * </div>
 *
 *
 * @author dan Lévy
 * @version 1.0
 * @since 9 déc. 2025
 */
package levy.daniel.application.model.metier.produittype;