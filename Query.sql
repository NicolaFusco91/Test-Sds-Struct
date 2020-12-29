select
  c_custkey,
  c_name,
  count(*) as ordsnum,
  sum(l_extendedprice*(1 - l_discount)) as revenue,
  c_acctbal,
  n_name,
  c_address,
  c_phone,
  c_comment
  from
  customer,
  orders,
  lineitem,
  nation
where c_custkey=o_custkey
  and l_orderkey=o_orderkey
  and (o_orderdate between '01/01/1993' and '31/03/1993'
    or o_orderdate>='01/07/1993' and o_orderdate<'01/10/1993')
  and l_returnflag in ('R', 'N')
  and c_nationkey=n_nationkey
group by
  c_custkey,
  c_name,
  c_acctbal,
  c_phone,
  n_name,
  c_address,
  c_comment
having
  ordsnum > 20
union all
select
  c_custkey,
  c_name,
  count(*) as ordsnum,
  sum(l_extendedprice*(1 - l_discount)) as revenue,
  c_acctbal,
  n_name,
  c_address,
  c_phone,
  c_comment
from
  customer inner join orders on (o_custkey=c_custkey),
  orders inner join lineitem on (l_orderkey=o_orderkey),
  customer inner join nation on (c_nationkey=n_nationkey)
where o_orderdate>='01/01/1993' and o_orderdate<'01/04/1993'
  and l_returnflag in ('R', 'N')
group by
  c_custkey,
  c_name,
  c_acctbal,
  c_phone,
  n_name,
  c_address,
  c_comment
having
  ordsnum = 1
order by
  revenue desc
;


