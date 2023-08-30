# SCTest

db.Position.aggregate([
  {
    $sort: { tradeDate: -1 } // Sort by tradeDate in descending order
  },
  {
    $group: {
      _id: { isin: "$isin", book: "$book" }, // Group by unique isin and book combination
      tradeDate: { $first: "$tradeDate" },   // Get the latest tradeDate for each group
      isin: { $first: "$isin" },             // Get the isin for each group
      book: { $first: "$book" },             // Get the book for each group
      _id_combination: { $first: "$_id" }    // Get the _id combination for each group
    }
  },
  {
    $sort: { tradeDate: -1 } // Sort by tradeDate in descending order again
  }
])
