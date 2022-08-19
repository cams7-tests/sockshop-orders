module.exports = (req, res, next) => {
  let send = res.send;
  res.send = function (response) {
    try {
      const data = JSON.parse(response);
      const declineOverAmount = 100.0;
      res.status(200);
      if(data.amount <= declineOverAmount)
        send.call(this, JSON.stringify({authorised: true,message: "Payment authorised"}));
      else
        send.call(this, JSON.stringify({authorised: false,message: `Payment declined: amount exceeds ${declineOverAmount.toFixed(2)}`}));
    } catch (err) {
      send.call(this, err);
    }
  };
  next();
}
