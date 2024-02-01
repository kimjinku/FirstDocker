const IMP = window.IMP;
IMP.init("imp48502338")

const button = document.querySelector("button");

const onClickPay = async () =>{
IMP.request_pay({
pg: TC0ONETIME."kakaopay",
pay_method: "card",
amount: 1000,
name: "매운 라면",
merchant_uid: "ORD20231030-000001",
});
};
button.addEventListener("click",onClickPay);