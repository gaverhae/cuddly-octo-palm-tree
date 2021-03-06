module Chapter2
where

-- # Chapter 2 - First steps
--
-- ## 2.1 - The Hugs system
--
-- This book is designed for Hugs but should work just as well with GHC.
--
-- ## 2.2 - The standard prelude
--
-- `Prelude.hs` is loaded automatically. Operators have priorities. Basic
-- operations on integers: `+`, `-`, `*`, `div`, `^`. Useful functions on lists:
-- `head`, `tail`, `!!`, `take`, `drop`, `length`, `sum`, `product`, `++`,
-- `reverse`.
--
-- ## 2.3 - Function application
--
-- Function application in Haskell is denoted by spacing, i.e. the math expression
-- $f(a,b)$ is written `f a b`; it has the highest priority and associates to the
-- left.
--
-- ## 2.4 - Haskell scripts
--
-- Hugs can load files with `:l`. `:r` will reload all loaded scripts. Haskell is
-- generally indentation-sensitive, though blocks can also be explicit with `{}`
-- and `;`. Line comments start with `--` and nested comments can be delimited by
-- `{- -}`.
--
-- ## 2.5 - Chapter remarks
--
-- See [haskell.org](https://www.haskell.org].
--
-- ## 2.6 - Exercises
--
-- > 1. Parenthesise the following arithmetic expressions:
-- > ```
-- > 2 ^ 3 * 4
-- > 2 * 3 + 4 * 5
-- > 2 + 3 * 4 ^ 5
-- > ```
--
-- ```
-- (2 ^ 3) * 4
-- (2 * 3) + (4 * 5)
-- 2 + (3 * (4 ^ 5))
-- ```
--
-- > 2. Work through the examples from this chapter using Hugs.
--
-- :shrug:
--
-- > 3. The script below contains three syntactic errors. Correct these errors and
-- > then check that your script works properly using Hugs.
-- > ```haskell
-- > N = a 'div' length xs
-- >   where a = 10
-- >        xs = [1,2,3,4,5]
-- > ```
--
-- ```haskell
-- n = a `div` length xs
--   where a = 10
--         xs = [1,2,3,4,5]
-- ```
--
-- > 4. Show how the library function `last` that selects the last element of a
-- > non-empty list could be defined in terms of the library functions introduced
-- > in this chapter. Can you think of another possible definition?
--
-- ```haskell
-- last xs = xs !! (length xs - 1)
-- ```
--
-- ```haskell
-- last xs = head (drop (length xs - 1) xs)
-- ```
--
-- ```haskell
-- last xs = head (reverse xs)
-- ```
--
-- > 5. Show how the library function `init` that removes the last element from a
-- > non-empty list could similarly be defined in two different ways.
--
-- ```haskell
-- init xs = take (length xs - 1) xs
-- ```
--
-- ```haskell
-- init xs = reverse (tail (reverse xs))
-- ````
