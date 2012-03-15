package pgbe.model
import net.liftweb.mapper.CreatedTrait
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK

trait WithAll[T <: WithAll[T]] extends LongKeyedMapper[T] with CreatedTrait with IdPK {
  self: T =>
}