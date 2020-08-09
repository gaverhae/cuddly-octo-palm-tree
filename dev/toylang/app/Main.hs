module Main where

import qualified Parser
import qualified Types

assert :: Types.ParseTree -> Types.ParseTree -> IO ()
assert actual expected =
  if actual == expected
  then do
      putStrLn $ "Successfully parsed " <> show actual
      return ()
  else
      error $ "Expected:\n" <> show expected <> "\nbut got:\n" <> show actual

main :: IO ()
main = do
  assert (Parser.read "2") (Types.Int 2)
  assert (Parser.read "(2)") (Types.App [Types.Int 2])
  assert (Parser.read "(concat \"hello \" \"world!\")") (Types.App [Types.Symbol "concat", Types.String "hello ", Types.String "world!"])
  assert (Parser.read "(test 4 (let [my-count (fn [v] (cond (= [] v) 0 true (+ 1 (my-count (rest v)))))] (my-count [1 2 3 4])))")
    (Types.App
      [(Types.Symbol "test"),
       (Types.Int 4),
       (Types.App
         [(Types.Symbol "let"),
          (Types.Vector
            [(Types.Symbol "my-count"),
             (Types.App
               [(Types.Symbol "fn"),
                (Types.Vector [(Types.Symbol "v")]),
                (Types.App
                  [(Types.Symbol "cond"),
                   (Types.App
                     [(Types.Symbol "="),
                      (Types.Vector []),
                      (Types.Symbol "v")]),
                   (Types.Int 0),
                   (Types.Boolean True),
                   (Types.App
                     [(Types.Symbol "+"),
                      (Types.Int 1),
                      (Types.App
                        [(Types.Symbol "my-count"),
                         (Types.App [(Types.Symbol "rest"),
                                     (Types.Symbol "v")])])])])])]),
          (Types.App
            [(Types.Symbol "my-count"),
             (Types.Vector
               [(Types.Int 1),
                (Types.Int 2),
                (Types.Int 3),
                (Types.Int 4)])])])])
