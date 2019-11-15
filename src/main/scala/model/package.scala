import shapeless.tag
import shapeless.tag.@@

/**
 * Created with IntelliJ IDEA.
 * User: Victor Mercurievv
 * Date: 11/11/2019
 * Time: 8:22 AM
 * Contacts: email: mercurievvss@gmail.com Skype: 'grobokopytoff' or 'mercurievv'
 */
package object model {
    trait CheckIdTag
    type CheckId = String @@ CheckIdTag
    object CheckId{
     def apply(o: String): CheckId = tag[CheckIdTag][String](o)
    }

}
