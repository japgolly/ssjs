package ssjs.db

import scala.scalajs.js
import scala.scalajs.js.|
import scala.scalajs.js.annotation._
import scala.annotation.nowarn

@js.native
@nowarn
trait PG extends js.Object {
  import PG._

  // @JSName("Client")
  // val ClientClass: ClientClass with js.Dynamic = js.native

  // val Client: ClientStatic = js.native
}

@nowarn
object PG {

  def Client(pg: PG): Client =
    Client(pg, js.undefined)

  def Client(pg: PG, configure: ClientConfig => Unit): Client = {
    val cfg = js.Dynamic.literal().asInstanceOf[ClientConfig]
    configure(cfg)
    Client(pg, cfg)
  }

  def Client(pg: PG, config: js.UndefOr[ClientConfig]): Client = {
    val cls = pg.asInstanceOf[js.Dynamic].Client
    js.Dynamic.newInstance(cls)(config).asInstanceOf[Client]
  }

  @js.native
  trait Client extends js.Object {
    def connect(): js.Promise[Unit]
    def end(): js.Promise[Unit]
    def query(text: String): js.Promise[Result]
    def query(q: QueryConfig): js.Promise[Result]
  }

  def QueryConfig() = js.Dynamic.literal().asInstanceOf[QueryConfig]

  @js.native
  trait QueryConfig extends js.Object {
    var text: String

    // an array of query parameters
    // var values?: Array<mixed>

    // name of the query - used for prepared statements
    // var name?: string;

    /* By default rows come out as a key/value pair for each row.
     * Pass the string 'array' here to receive rows as an array of values
     */
    var rowMode: js.UndefOr["array"]

    /* custom type parsers just for this query result */
    // var types?: Types;
  }

  @js.native
  trait Result extends js.Object {
    val command: String = js.native
    val rowCount: Int = js.native
    val rows: js.Array[Row] = js.native
    val fields: js.Array[FieldInfo] = js.native
  }

  type ArrayRow = js.Array[Any]
  type NamedRow = js.Dynamic
  type Row = ArrayRow | NamedRow

  @js.native
  trait FieldInfo extends js.Object {
    val name: String = js.native
    val dataTypeId: Any = js.native
  }

  @js.native
  trait ClientConfig extends js.Object {

    /** default process.env.PGUSER || process.env.USER */
    var user: js.UndefOr[String]

    /** default process.env.PGPASSWORD */
    var password: js.UndefOr[String]

    /** default process.env.PGHOST */
    var host: js.UndefOr[String]

    /** default process.env.PGDATABASE || process.env.USER */
    var database: js.UndefOr[String]

    /** default process.env.PGPORT */
    var port: js.UndefOr[Int]

    /** e.g. postgres://user:password@host:5432/database */
    var connectionString: js.UndefOr[String]

    /** passed directly to node.TLSSocket, supports all tls.connect options */
    var ssl: js.UndefOr[Any]

    /** custom type parsers */
    var types: js.UndefOr[Any]

    /** number of milliseconds before a statement in query will time out, default is no timeout */
    @JSName("statement_timeout")
    var statementTimeout: js.UndefOr[Int]

    /** number of milliseconds before a query call will timeout, default is no timeout */
    @JSName("query_timeout")
    var queryTimeout: js.UndefOr[Int]

    /** number of milliseconds to wait for connection, default is no timeout */
    var connectionTimeoutMillis: js.UndefOr[Int]

    /** number of milliseconds before terminating any session with an open idle transaction, default is no timeout */
    @JSName("idle_in_transaction_session_timeout")
    var idleInTransactionSessionTimeout: js.UndefOr[Int]//
  }
}
